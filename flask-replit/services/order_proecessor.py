import threading
import time
from datetime import datetime
from models import orders, OrderStatus, OrderType, OrderSide
from services.stock_data_service import StockDataService
import logging


class OrderProcessor:
    def __init__(self):
        self.stock_data_service = StockDataService()
        self.processing = False
        self.thread = None

    def start_processing(self):
        """Start background order processing thread"""
        if not self.processing:
            self.processing = True
            self.thread = threading.Thread(target=self._process_orders, daemon=True)
            self.thread.start()
            logging.info("Order processor started")

    def stop_processing(self):
        """Stop background order processing"""
        self.processing = False
        if self.thread:
            self.thread.join()
        logging.info("Order processor stopped")

    def _process_orders(self):
        """Background process to handle limit orders"""
        from services.trading_service import TradingService
        trading_service = TradingService()

        while self.processing:
            try:
                # Process all pending limit orders
                pending_orders = [order for order in orders.values()
                                  if order.status == OrderStatus.PENDING and order.order_type == OrderType.LIMIT]

                for order in pending_orders:
                    self._check_limit_order(order, trading_service)

                # Sleep for a short interval before checking again
                time.sleep(1)

            except Exception as e:
                logging.error(f"Error in order processor: {e}")
                time.sleep(5)  # Wait longer on error

    def _check_limit_order(self, order, trading_service):
        """Check if a limit order should be executed"""
        try:
            current_price = self.stock_data_service.get_current_price(order.ticker)
            if current_price is None:
                return

            should_execute = False

            if order.side == OrderSide.BUY:
                # Buy limit order executes when current price <= limit price
                should_execute = current_price <= order.price
            else:  # SELL
                # Sell limit order executes when current price >= limit price
                should_execute = current_price >= order.price

            if should_execute:
                logging.info(f"Executing limit order {order.order_id} at price {current_price}")
                trading_service._execute_order(order, current_price)

        except Exception as e:
            logging.error(f"Error checking limit order {order.order_id}: {e}")
            order.status = OrderStatus.REJECTED
