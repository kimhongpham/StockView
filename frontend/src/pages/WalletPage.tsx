import React from 'react';
import { transactionData } from '../data/mockData';

export const WalletPage: React.FC = () => {
  return (
    <div className="page" id="wallet">
      <h1 className="page-title">Wallet</h1>

      {/* Wallet Stats */}
      <div className="wallet-stats">
        <div className="wallet-stat">
          <div className="kpi-title">Total Investment</div>
          <div className="kpi-value">$211,111.00</div>
        </div>
        <div className="wallet-stat">
          <div className="kpi-title">Total Return</div>
          <div className="kpi-value positive">+16.37%</div>
        </div>
        <div className="wallet-stat">
          <div className="kpi-title">All Time Profit/Loss</div>
          <div className="kpi-value positive">+$34,567.90</div>
        </div>
      </div>

      {/* Wallet Layout */}
      <div className="wallet-layout">
        {/* Transaction History */}
        <div className="chart-container">
          <h2 className="chart-title">Transaction History</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Amount</th>
                <th>Date</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {transactionData.map((transaction) => (
                <tr key={transaction.id}>
                  <td>{transaction.id}</td>
                  <td>{transaction.amount}</td>
                  <td>{transaction.date}</td>
                  <td className={transaction.status === 'Success' ? 'positive' : ''}>
                    {transaction.status}
                  </td>
                  <td>
                    <button className="btn btn-secondary" style={{ padding: '5px 10px' }}>
                      Download
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Payment Form */}
        <div className="payment-form">
          <h2 className="chart-title">Payment</h2>

          <div className="form-group">
            <label htmlFor="paymentCoin">Coin</label>
            <select id="paymentCoin">
              <option value="BTC">Bitcoin (BTC)</option>
              <option value="ETH">Ethereum (ETH)</option>
              <option value="USDT">Tether (USDT)</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="paymentAmount">Amount</label>
            <input
              type="text"
              id="paymentAmount"
              placeholder="Enter amount"
            />
          </div>

          <div className="payment-methods">
            <div className="payment-method active">Visa</div>
            <div className="payment-method">MasterCard</div>
          </div>

          <div className="form-group">
            <label htmlFor="cardNumber">Card Number</label>
            <input
              type="text"
              id="cardNumber"
              placeholder="1234 5678 9012 3456"
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
            <div className="form-group">
              <label htmlFor="expiration">Expiration</label>
              <input type="text" id="expiration" placeholder="MM/YY" />
            </div>
            <div className="form-group">
              <label htmlFor="cvv">CVV</label>
              <input type="text" id="cvv" placeholder="123" />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="paymentEmail">Email</label>
            <input
              type="email"
              id="paymentEmail"
              placeholder="your@email.com"
            />
          </div>

          <button className="btn btn-primary" style={{ width: '100%' }}>Save</button>
        </div>
      </div>
    </div>
  );
};