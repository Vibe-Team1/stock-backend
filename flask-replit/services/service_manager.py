"""
Centralized service manager to ensure singleton instances
"""


class ServiceManager:
    _instance = None
    _services = {}

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(ServiceManager, cls).__new__(cls)
        return cls._instance

    def get_stock_data_service(self):
        if 'stock_data' not in self._services:
            from services.stock_data_service import StockDataService
            self._services['stock_data'] = StockDataService()
            self._services['stock_data'].load_historical_data()
        return self._services['stock_data']

    def get_trading_service(self):
        if 'trading' not in self._services:
            from services.trading_service import TradingService
            self._services['trading'] = TradingService()
        return self._services['trading']

    def get_portfolio_service(self):
        if 'portfolio' not in self._services:
            from services.portfolio_service import PortfolioService
            self._services['portfolio'] = PortfolioService()
        return self._services['portfolio']

    def get_order_processor(self):
        if 'order_processor' not in self._services:
            from services.order_processor import OrderProcessor
            self._services['order_processor'] = OrderProcessor()
            self._services['order_processor'].start_processing()
        return self._services['order_processor']


# Global service manager instance
service_manager = ServiceManager()