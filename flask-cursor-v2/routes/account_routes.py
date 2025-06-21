from flask import Blueprint, request, jsonify
from models.user import User
from models.portfolio import PortfolioItem
from app import db

account_bp = Blueprint('account', __name__)

@account_bp.route('/account', methods=['GET'])
def get_account():
    """Get user account information"""
    try:
        user_id = request.args.get('user_id', type=int)
        if not user_id:
            return jsonify({'error': 'user_id parameter is required'}), 400
        
        user = User.query.get(user_id)
        if not user:
            return jsonify({'error': 'User not found'}), 404
        
        return jsonify({
            'account': user.to_dict()
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@account_bp.route('/account', methods=['POST'])
def create_account():
    """Create a new user account"""
    try:
        data = request.get_json()
        
        # Validate required fields
        required_fields = ['username', 'email']
        for field in required_fields:
            if field not in data:
                return jsonify({'error': f'Missing required field: {field}'}), 400
        
        username = data['username']
        email = data['email']
        initial_balance = data.get('initial_balance', 10000000.0)  # Default 10M KRW
        
        # Check if user already exists
        existing_user = User.query.filter(
            (User.username == username) | (User.email == email)
        ).first()
        
        if existing_user:
            return jsonify({'error': 'Username or email already exists'}), 409
        
        # Create new user
        user = User(
            username=username,
            email=email,
            initial_balance=initial_balance,
            available_balance=initial_balance
        )
        
        db.session.add(user)
        db.session.commit()
        
        return jsonify({
            'message': 'Account created successfully',
            'account': user.to_dict()
        }), 201
        
    except Exception as e:
        db.session.rollback()
        return jsonify({'error': 'Internal server error'}), 500

@account_bp.route('/portfolio', methods=['GET'])
def get_portfolio():
    """Get user portfolio"""
    try:
        user_id = request.args.get('user_id', type=int)
        if not user_id:
            return jsonify({'error': 'user_id parameter is required'}), 400
        
        user = User.query.get(user_id)
        if not user:
            return jsonify({'error': 'User not found'}), 404
        
        portfolio_items = PortfolioItem.query.filter_by(user_id=user_id).all()
        
        # Calculate portfolio summary
        total_invested = sum(item.total_invested for item in portfolio_items)
        total_current_value = sum(item.current_value for item in portfolio_items)
        total_profit_loss = total_current_value - total_invested
        
        return jsonify({
            'portfolio': {
                'items': [item.to_dict() for item in portfolio_items],
                'summary': {
                    'total_items': len(portfolio_items),
                    'total_invested': total_invested,
                    'total_current_value': total_current_value,
                    'total_profit_loss': total_profit_loss,
                    'total_profit_loss_percentage': (total_profit_loss / total_invested * 100) if total_invested > 0 else 0,
                    'available_balance': user.available_balance,
                    'total_portfolio_value': user.calculate_total_portfolio_value()
                }
            }
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@account_bp.route('/portfolio/<ticker>', methods=['GET'])
def get_portfolio_item(ticker):
    """Get specific portfolio item"""
    try:
        user_id = request.args.get('user_id', type=int)
        if not user_id:
            return jsonify({'error': 'user_id parameter is required'}), 400
        
        portfolio_item = PortfolioItem.query.filter_by(
            user_id=user_id, 
            ticker=ticker
        ).first()
        
        if not portfolio_item:
            return jsonify({'error': 'Portfolio item not found'}), 404
        
        return jsonify({
            'portfolio_item': portfolio_item.to_dict()
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500 