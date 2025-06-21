from flask import Blueprint, request, jsonify
from datetime import datetime
from models import OrderType, OrderSide
from services.service_manager import service_manager

api_bp = Blueprint('api', __name__)


@api_bp.route('/trade/buy', methods=['POST'])
def buy_stock():
    """Place a buy order"""
    try:
        data = request.get_json()
        user_id = data.get('user_id')
        ticker = data.get('ticker')
        quantity = int(data.get('quantity'))
        order_type = OrderType(data.get('order_type', 'MARKET'))
        price = float(data.get('price')) if data.get('price') else None

        result = service_manager.get_trading_service().place_buy_order(user_id, ticker, quantity, order_type, price)

        return jsonify(result), 200 if result['success'] else 400

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@api_bp.route('/trade/sell', methods=['POST'])
def sell_stock():
    """Place a sell order"""
    try:
        data = request.get_json()
        user_id = data.get('user_id')
        ticker = data.get('ticker')
        quantity = int(data.get('quantity'))
        order_type = OrderType(data.get('order_type', 'MARKET'))
        price = float(data.get('price')) if data.get('price') else None

        result = service_manager.get_trading_service().place_sell_order(user_id, ticker, quantity, order_type, price)

        return jsonify(result), 200 if result['success'] else 400

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@api_bp.route('/account/<user_id>', methods=['GET'])
def get_account(user_id):
    """Get account information"""
    try:
        account_summary = service_manager.get_portfolio_service().get_account_summary(user_id)
        if not account_summary:
            return jsonify({"success": False, "message": "User not found"}), 404

        return jsonify({"success": True, "data": account_summary}), 200

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@api_bp.route('/orders/<user_id>', methods=['GET'])
def get_orders(user_id):
    """Get user's orders (pending and history)"""
    try:
        pending_orders = service_manager.get_trading_service().get_pending_orders(user_id)
        order_history = service_manager.get_trading_service().get_order_history(user_id)

        # Convert orders to dict format
        pending_data = []
        for order in pending_orders:
            pending_data.append({
                "order_id": order.order_id,
                "ticker": order.ticker,
                "side": order.side.value,
                "order_type": order.order_type.value,
                "quantity": order.quantity,
                "price": order.price,
                "status": order.status.value,
                "created_at": order.created_at.isoformat()
            })

        history_data = []
        for order in order_history:
            history_data.append({
                "order_id": order.order_id,
                "ticker": order.ticker,
                "side": order.side.value,
                "order_type": order.order_type.value,
                "quantity": order.quantity,
                "price": order.price,
                "filled_price": order.filled_price,
                "filled_quantity": order.filled_quantity,
                "status": order.status.value,
                "created_at": order.created_at.isoformat(),
                "filled_at": order.filled_at.isoformat() if order.filled_at else None
            })

        return jsonify({
            "success": True,
            "data": {
                "pending_orders": pending_data,
                "order_history": history_data
            }
        }), 200

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@api_bp.route('/stock/<ticker>/price', methods=['GET'])
def get_stock_price(ticker):
    """Get stock price, optionally at a specific timestamp"""
    try:
        timestamp_str = request.args.get('at')

        if timestamp_str:
            timestamp = datetime.fromisoformat(timestamp_str.replace('Z', '+00:00'))
            price = service_manager.get_stock_data_service().get_price_at_timestamp(ticker, timestamp)
        else:
            price = service_manager.get_stock_data_service().get_current_price(ticker)

        if price is None:
            return jsonify({"success": False, "message": "Stock not found"}), 404

        stock_info = service_manager.get_stock_data_service().get_stock_info(ticker)

        return jsonify({
            "success": True,
            "data": {
                "ticker": ticker,
                "price": price,
                "timestamp": timestamp_str or datetime.now().isoformat(),
                "stock_info": stock_info
            }
        }), 200

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@api_bp.route('/stocks', methods=['GET'])
def get_stocks():
    """Get all available stocks"""
    try:
        query = request.args.get('search', '')

        if query:
            stocks = service_manager.get_stock_data_service().search_stocks(query)
        else:
            stocks = service_manager.get_stock_data_service().get_available_stocks()

        return jsonify({"success": True, "data": stocks}), 200

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@api_bp.route('/users', methods=['POST'])
def create_user():
    """Create a new user"""
    try:
        data = request.get_json()
        username = data.get('username')
        email = data.get('email')
        initial_cash = float(data.get('initial_cash', 100000.0))

        if not username or not email:
            return jsonify({"success": False, "message": "Username and email required"}), 400

        user = service_manager.get_trading_service().create_user(username, email, initial_cash)

        return jsonify({
            "success": True,
            "data": {
                "user_id": user.user_id,
                "username": user.username,
                "email": user.email,
                "initial_cash": user.initial_cash
            }
        }), 201

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@api_bp.route('/orders/<user_id>/<order_id>/cancel', methods=['POST'])
def cancel_order(user_id, order_id):
    """Cancel a pending order"""
    try:
        result = service_manager.get_trading_service().cancel_order(user_id, order_id)
        return jsonify(result), 200 if result['success'] else 400

    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500
