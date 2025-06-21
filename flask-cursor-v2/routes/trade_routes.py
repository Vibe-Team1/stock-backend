from flask import Blueprint, request, jsonify
from services.trading_service import TradingService
from models.order import Order, OrderStatus
from app import db
from datetime import datetime

trade_bp = Blueprint('trade', __name__)

@trade_bp.route('/buy', methods=['POST'])
def buy_order():
    """Place a buy order"""
    try:
        data = request.get_json()
        
        # Validate required fields
        required_fields = ['user_id', 'ticker', 'quantity', 'order_type']
        for field in required_fields:
            if field not in data:
                return jsonify({'error': f'Missing required field: {field}'}), 400
        
        user_id = data['user_id']
        ticker = data['ticker']
        quantity = data['quantity']
        order_type = data['order_type']
        limit_price = data.get('limit_price')
        
        # Validate order type
        if order_type not in ['MARKET', 'LIMIT']:
            return jsonify({'error': 'Invalid order type. Must be MARKET or LIMIT'}), 400
        
        # Validate limit price for LIMIT orders
        if order_type == 'LIMIT' and not limit_price:
            return jsonify({'error': 'Limit price required for LIMIT orders'}), 400
        
        # Place the order
        order = TradingService.place_buy_order(
            user_id=user_id,
            ticker=ticker,
            quantity=quantity,
            order_type=order_type,
            limit_price=limit_price
        )
        
        return jsonify({
            'message': 'Buy order placed successfully',
            'order': order
        }), 201
        
    except ValueError as e:
        return jsonify({'error': str(e)}), 400
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@trade_bp.route('/sell', methods=['POST'])
def sell_order():
    """Place a sell order"""
    try:
        data = request.get_json()
        
        # Validate required fields
        required_fields = ['user_id', 'ticker', 'quantity', 'order_type']
        for field in required_fields:
            if field not in data:
                return jsonify({'error': f'Missing required field: {field}'}), 400
        
        user_id = data['user_id']
        ticker = data['ticker']
        quantity = data['quantity']
        order_type = data['order_type']
        limit_price = data.get('limit_price')
        
        # Validate order type
        if order_type not in ['MARKET', 'LIMIT']:
            return jsonify({'error': 'Invalid order type. Must be MARKET or LIMIT'}), 400
        
        # Validate limit price for LIMIT orders
        if order_type == 'LIMIT' and not limit_price:
            return jsonify({'error': 'Limit price required for LIMIT orders'}), 400
        
        # Place the order
        order = TradingService.place_sell_order(
            user_id=user_id,
            ticker=ticker,
            quantity=quantity,
            order_type=order_type,
            limit_price=limit_price
        )
        
        return jsonify({
            'message': 'Sell order placed successfully',
            'order': order
        }), 201
        
    except ValueError as e:
        return jsonify({'error': str(e)}), 400
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@trade_bp.route('/orders', methods=['GET'])
def get_orders():
    """Get all orders for a user"""
    try:
        user_id = request.args.get('user_id', type=int)
        if not user_id:
            return jsonify({'error': 'user_id parameter is required'}), 400
        
        # Get query parameters for filtering
        status = request.args.get('status')
        ticker = request.args.get('ticker')
        limit = request.args.get('limit', type=int, default=50)
        
        # Build query
        query = Order.query.filter_by(user_id=user_id)
        
        if status:
            query = query.filter_by(status=OrderStatus(status))
        
        if ticker:
            query = query.filter_by(ticker=ticker)
        
        # Order by creation date (newest first)
        query = query.order_by(Order.created_at.desc()).limit(limit)
        
        orders = query.all()
        
        return jsonify({
            'orders': [order.to_dict() for order in orders],
            'total': len(orders)
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@trade_bp.route('/orders/<int:order_id>', methods=['GET'])
def get_order(order_id):
    """Get a specific order by ID"""
    try:
        order = Order.query.get(order_id)
        if not order:
            return jsonify({'error': 'Order not found'}), 404
        
        return jsonify({
            'order': order.to_dict()
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@trade_bp.route('/orders/<int:order_id>/cancel', methods=['POST'])
def cancel_order(order_id):
    """Cancel a pending order"""
    try:
        order = Order.query.get(order_id)
        if not order:
            return jsonify({'error': 'Order not found'}), 404
        
        if order.status != OrderStatus.PENDING:
            return jsonify({'error': 'Only pending orders can be cancelled'}), 400
        
        order.cancel()
        db.session.commit()
        
        return jsonify({
            'message': 'Order cancelled successfully',
            'order': order.to_dict()
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500 