#!/usr/bin/env python3
"""
Test script for the Stock Trading Simulation Platform API
"""

import requests
import json
import time
from datetime import datetime

BASE_URL = "http://localhost:5000/api"

def test_create_user():
    """Test creating a new user account"""
    print("=== Testing User Creation ===")
    
    user_data = {
        "username": "test_trader",
        "email": "test@example.com",
        "initial_balance": 10000000  # 10M KRW
    }
    
    response = requests.post(f"{BASE_URL}/account", json=user_data)
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    
    if response.status_code == 201:
        return response.json()['account']['id']
    return None

def test_get_account(user_id):
    """Test getting user account information"""
    print(f"\n=== Testing Get Account (User ID: {user_id}) ===")
    
    response = requests.get(f"{BASE_URL}/account?user_id={user_id}")
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")

def test_get_stock_price():
    """Test getting stock price"""
    print("\n=== Testing Get Stock Price ===")
    
    # Test current price
    response = requests.get(f"{BASE_URL}/stock/005930/price")
    print(f"Current Price Status: {response.status_code}")
    print(f"Current Price Response: {json.dumps(response.json(), indent=2)}")
    
    # Test historical price
    timestamp = "2025-06-17 14:30:00"
    response = requests.get(f"{BASE_URL}/stock/005930/price?at={timestamp}")
    print(f"Historical Price Status: {response.status_code}")
    print(f"Historical Price Response: {json.dumps(response.json(), indent=2)}")

def test_get_stock_list():
    """Test getting list of available stocks"""
    print("\n=== Testing Get Stock List ===")
    
    response = requests.get(f"{BASE_URL}/stock/list")
    print(f"Status: {response.status_code}")
    data = response.json()
    print(f"Total stocks: {data['total']}")
    print(f"First 3 stocks: {json.dumps(data['stocks'][:3], indent=2)}")

def test_place_buy_order(user_id):
    """Test placing a buy order"""
    print(f"\n=== Testing Buy Order (User ID: {user_id}) ===")
    
    order_data = {
        "user_id": user_id,
        "ticker": "005930",  # Samsung Electronics
        "quantity": 10,
        "order_type": "MARKET"
    }
    
    response = requests.post(f"{BASE_URL}/trade/buy", json=order_data)
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")
    
    return response.json().get('order', {}).get('id') if response.status_code == 201 else None

def test_place_limit_buy_order(user_id):
    """Test placing a limit buy order"""
    print(f"\n=== Testing Limit Buy Order (User ID: {user_id}) ===")
    
    order_data = {
        "user_id": user_id,
        "ticker": "000660",  # SK Hynix
        "quantity": 5,
        "order_type": "LIMIT",
        "limit_price": 250000  # 250,000 KRW
    }
    
    response = requests.post(f"{BASE_URL}/trade/buy", json=order_data)
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")

def test_get_orders(user_id):
    """Test getting user orders"""
    print(f"\n=== Testing Get Orders (User ID: {user_id}) ===")
    
    response = requests.get(f"{BASE_URL}/trade/orders?user_id={user_id}")
    print(f"Status: {response.status_code}")
    data = response.json()
    print(f"Total orders: {data['total']}")
    if data['orders']:
        print(f"First order: {json.dumps(data['orders'][0], indent=2)}")

def test_get_portfolio(user_id):
    """Test getting user portfolio"""
    print(f"\n=== Testing Get Portfolio (User ID: {user_id}) ===")
    
    response = requests.get(f"{BASE_URL}/portfolio?user_id={user_id}")
    print(f"Status: {response.status_code}")
    data = response.json()
    print(f"Portfolio summary: {json.dumps(data['portfolio']['summary'], indent=2)}")
    if data['portfolio']['items']:
        print(f"Portfolio items: {json.dumps(data['portfolio']['items'], indent=2)}")

def test_place_sell_order(user_id):
    """Test placing a sell order"""
    print(f"\n=== Testing Sell Order (User ID: {user_id}) ===")
    
    order_data = {
        "user_id": user_id,
        "ticker": "005930",  # Samsung Electronics
        "quantity": 5,
        "order_type": "MARKET"
    }
    
    response = requests.post(f"{BASE_URL}/trade/sell", json=order_data)
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")

def test_get_stock_info():
    """Test getting stock information"""
    print("\n=== Testing Get Stock Info ===")
    
    response = requests.get(f"{BASE_URL}/stock/005930/info")
    print(f"Status: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2)}")

def main():
    """Run all tests"""
    print("Stock Trading Simulation Platform - API Test")
    print("=" * 50)
    
    try:
        # Test stock information first
        test_get_stock_list()
        test_get_stock_price()
        test_get_stock_info()
        
        # Test user account
        user_id = test_create_user()
        if user_id:
            test_get_account(user_id)
            
            # Test trading
            test_place_buy_order(user_id)
            test_place_limit_buy_order(user_id)
            test_get_orders(user_id)
            test_get_portfolio(user_id)
            
            # Wait a bit before selling
            time.sleep(1)
            test_place_sell_order(user_id)
            
            # Check final state
            test_get_portfolio(user_id)
            test_get_orders(user_id)
        
        print("\n" + "=" * 50)
        print("All tests completed!")
        
    except requests.exceptions.ConnectionError:
        print("Error: Could not connect to the server. Make sure the Flask app is running on http://localhost:5000")
    except Exception as e:
        print(f"Error during testing: {e}")

if __name__ == "__main__":
    main() 