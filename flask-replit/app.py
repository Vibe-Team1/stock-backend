import os
import logging
from flask import Flask
from werkzeug.middleware.proxy_fix import ProxyFix

# Configure logging
logging.basicConfig(level=logging.DEBUG)

# Create the Flask app
app = Flask(__name__)
app.secret_key = os.environ.get("SESSION_SECRET", "default_secret_key_for_dev")
app.wsgi_app = ProxyFix(app.wsgi_app, x_proto=1, x_host=1)

# Import and register blueprints
from routes.api_routes import api_bp

app.register_blueprint(api_bp, url_prefix='/api')

# Initialize services using service manager
from services.service_manager import service_manager

# Initialize all services
service_manager.get_stock_data_service()
service_manager.get_order_processor()

# Add a simple root endpoint
@app.route('/')
def index():
    return {
        "message": "Stock Trading Simulation API",
        "version": "1.0",
        "endpoints": {
            "POST /api/users": "Create a new user",
            "POST /api/trade/buy": "Place a buy order",
            "POST /api/trade/sell": "Place a sell order",
            "GET /api/account/<user_id>": "Get account information",
            "GET /api/orders/<user_id>": "Get user's orders",
            "GET /api/stock/<ticker>/price": "Get stock price",
            "GET /api/stocks": "Get all available stocks",
            "POST /api/orders/<user_id>/<order_id>/cancel": "Cancel a pending order"
        }
    }
