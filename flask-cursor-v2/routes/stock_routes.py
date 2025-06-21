from flask import Blueprint, request, jsonify
from services.stock_service import StockService
from datetime import datetime

stock_bp = Blueprint('stock', __name__)

@stock_bp.route('/<ticker>/price', methods=['GET'])
def get_stock_price(ticker):
    """Get stock price at a specific time or current price"""
    try:
        # Get timestamp parameter if provided
        timestamp_str = request.args.get('at')
        
        if timestamp_str:
            try:
                # Parse timestamp (expecting ISO format or YYYY-MM-DD HH:MM:SS)
                if 'T' in timestamp_str:
                    timestamp = datetime.fromisoformat(timestamp_str.replace('Z', '+00:00'))
                else:
                    timestamp = datetime.strptime(timestamp_str, '%Y-%m-%d %H:%M:%S')
                
                price = StockService.get_price_at_time(ticker, timestamp)
            except ValueError:
                return jsonify({'error': 'Invalid timestamp format. Use ISO format or YYYY-MM-DD HH:MM:SS'}), 400
        else:
            # Get current price
            price = StockService.get_current_price(ticker)
        
        if price is None:
            return jsonify({'error': 'Price not available for this ticker'}), 404
        
        stock_info = StockService.get_stock_info(ticker)
        
        return jsonify({
            'ticker': ticker,
            'stock_name': stock_info.get('종목명') if stock_info else None,
            'price': price,
            'timestamp': timestamp_str if timestamp_str else 'current'
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@stock_bp.route('/<ticker>/info', methods=['GET'])
def get_stock_info(ticker):
    """Get stock basic information"""
    try:
        stock_info = StockService.get_stock_info(ticker)
        
        if not stock_info:
            return jsonify({'error': 'Stock not found'}), 404
        
        return jsonify({
            'ticker': ticker,
            'info': stock_info
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@stock_bp.route('/<ticker>/prices', methods=['GET'])
def get_stock_prices(ticker):
    """Get stock price data within a time range"""
    try:
        start_time_str = request.args.get('start_time')
        end_time_str = request.args.get('end_time')
        
        if not start_time_str or not end_time_str:
            return jsonify({'error': 'start_time and end_time parameters are required'}), 400
        
        try:
            # Parse timestamps
            if 'T' in start_time_str:
                start_time = datetime.fromisoformat(start_time_str.replace('Z', '+00:00'))
            else:
                start_time = datetime.strptime(start_time_str, '%Y-%m-%d %H:%M:%S')
            
            if 'T' in end_time_str:
                end_time = datetime.fromisoformat(end_time_str.replace('Z', '+00:00'))
            else:
                end_time = datetime.strptime(end_time_str, '%Y-%m-%d %H:%M:%S')
                
        except ValueError:
            return jsonify({'error': 'Invalid timestamp format. Use ISO format or YYYY-MM-DD HH:MM:SS'}), 400
        
        prices = StockService.get_price_range(ticker, start_time, end_time)
        
        return jsonify({
            'ticker': ticker,
            'start_time': start_time_str,
            'end_time': end_time_str,
            'prices': prices,
            'count': len(prices)
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500

@stock_bp.route('/list', methods=['GET'])
def get_stock_list():
    """Get list of all available stocks"""
    try:
        tickers = StockService.get_all_tickers()
        
        # Get basic info for each ticker
        stocks = []
        for ticker in tickers:
            stock_info = StockService.get_stock_info(ticker)
            current_price = StockService.get_current_price(ticker)
            
            stocks.append({
                'ticker': ticker,
                'name': stock_info.get('종목명') if stock_info else None,
                'current_price': current_price,
                'market_cap': stock_info.get('시가총액(억)') if stock_info else None,
                'per': stock_info.get('PER') if stock_info else None,
                'pbr': stock_info.get('PBR') if stock_info else None
            })
        
        return jsonify({
            'stocks': stocks,
            'total': len(stocks)
        }), 200
        
    except Exception as e:
        return jsonify({'error': 'Internal server error'}), 500 