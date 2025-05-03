package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pchub.model.Bill;
import pchub.model.PaymentMethod;
import pchub.model.Transaction;
import pchub.model.enums.PaymentStatus;
import pchub.utils.DatabaseConnection;

public class TransactionDao extends DaoTemplate<Transaction> {

    private Connection connection;
    private BillDao billDao;
    private PaymentMethodDao paymentMethodDao;

    /**
     * Constructor initializes database connection and required DAOs
     * @throws SQLException if a database access error occurs
     */
    public TransactionDao(){
        this.connection = DatabaseConnection.getConnection();
        this.billDao = new BillDao();
        this.paymentMethodDao = new PaymentMethodDao();
    }

    /**
     * Find a transaction by its ID
     * @param id the transaction ID
     * @return the Transaction object or null if not found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Transaction findById(String id) throws SQLException {
        Transaction transaction = null;
        String sql = "SELECT * FROM transaction WHERE transactionID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    transaction = mapResultSet(resultSet);
                }
            }
        }

        return transaction;
    }

    /**
     * Insert a new transaction into the database
     * @param transaction the Transaction object to insert
     * @return true if insertion was successful
     * @throws SQLException if a database access error occurs
     */
    @Override
    public boolean insert(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transaction (billID, amount, payment_methodID, " +
                "status, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transaction.getBill().getBillId());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setString(3, transaction.getPaymentMethod().getPaymentMethodId());
            preparedStatement.setString(4, transaction.getStatus().name());
            preparedStatement.setString(5, transaction.getDescription());

            int rowsAffected = preparedStatement.executeUpdate();

            // Get the auto-generated ID if insert was successful
            if (rowsAffected > 0) {
                String generatedId = getLastInsertedTransactionId();
                transaction.setTransactionId(generatedId);
            }

            return rowsAffected > 0;
        }
    }

    /**
     * Get the last inserted transaction ID
     * @return the last inserted transaction ID
     * @throws SQLException if a database access error occurs
     */
    private String getLastInsertedTransactionId() throws SQLException {
        String sql = "SELECT transactionID FROM transaction ORDER BY transactionDate DESC LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("transactionID");
            }
        }

        return null;
    }

    /**
     * Update an existing transaction in the database
     * @param transaction the Transaction object with updated values
     * @return true if update was successful
     * @throws SQLException if a database access error occurs
     */
    @Override
    public boolean update(Transaction transaction) throws SQLException {
        String sql = "UPDATE transaction SET billID = ?, amount = ?, payment_methodID = ?, " +
                "status = ?, description = ? " +
                "WHERE transactionID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transaction.getBill().getBillId());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setString(3, transaction.getPaymentMethod().getPaymentMethodId());
            preparedStatement.setString(4, transaction.getStatus().name());
            preparedStatement.setString(5, transaction.getDescription());
            preparedStatement.setString(6, transaction.getTransactionId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Delete a transaction from the database
     * @param id the transaction ID to delete
     * @return true if deletion was successful
     * @throws SQLException if a database access error occurs
     */
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM transaction WHERE transactionID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Maps a ResultSet row to a Transaction object
     * @param resultSet the result set to map
     * @return a Transaction object
     * @throws SQLException if a database access error occurs
     */
    @Override
    protected Transaction mapResultSet(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();

        transaction.setTransactionId(resultSet.getString("transactionID"));

        // Fetch the associated Bill
        String billId = resultSet.getString("billID");
        Bill bill = billDao.findById(billId);
        transaction.setBill(bill);

        transaction.setAmount(resultSet.getBigDecimal("amount"));

        // Fetch the associated PaymentMethod
        String paymentMethodId = resultSet.getString("payment_methodID");
        PaymentMethod paymentMethod = paymentMethodDao.findById(paymentMethodId);
        transaction.setPaymentMethod(paymentMethod);

        transaction.setStatus(PaymentStatus.valueOf(resultSet.getString("status")));
        transaction.setTransactionDate(resultSet.getTimestamp("transactionDate"));
        transaction.setLastModifiedDate(resultSet.getTimestamp("lastModifiedDate"));
        transaction.setDescription(resultSet.getString("description"));

        return transaction;
    }

    /**
     * Find all transactions for a specific bill
     * @param billId the bill ID
     * @return an array of Transaction objects
     * @throws SQLException if a database access error occurs
     */
    public Transaction[] findByBillId(String billId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM transaction WHERE billID = ?";
        int count = 0;

        // First get the count to create appropriate sized array
        try (PreparedStatement countStmt = connection.prepareStatement(sql)) {
            countStmt.setString(1, billId);
            try (ResultSet countRs = countStmt.executeQuery()) {
                if (countRs.next()) {
                    count = countRs.getInt("count");
                }
            }
        }

        Transaction[] transactions = new Transaction[count];

        sql = "SELECT * FROM transaction WHERE billID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, billId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int index = 0;
                while (resultSet.next()) {
                    transactions[index++] = mapResultSet(resultSet);
                }
            }
        }

        return transactions;
    }

    /**
     * Find transactions by their payment status
     * @param status the payment status
     * @return an array of Transaction objects
     * @throws SQLException if a database access error occurs
     */
    public Transaction[] findByStatus(PaymentStatus status) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM transaction WHERE status = ?";
        int count = 0;

        // First get the count to create appropriate sized array
        try (PreparedStatement countStmt = connection.prepareStatement(sql)) {
            countStmt.setString(1, status.name());
            try (ResultSet countRs = countStmt.executeQuery()) {
                if (countRs.next()) {
                    count = countRs.getInt("count");
                }
            }
        }

        Transaction[] transactions = new Transaction[count];

        sql = "SELECT * FROM transaction WHERE status = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status.name());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int index = 0;
                while (resultSet.next()) {
                    transactions[index++] = mapResultSet(resultSet);
                }
            }
        }

        return transactions;
    }

    /**
     * Find transactions by payment method ID
     * @param paymentMethodId the payment method ID
     * @return an array of Transaction objects
     * @throws SQLException if a database access error occurs
     */
    public Transaction[] findByPaymentMethodId(String paymentMethodId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM transaction WHERE payment_methodID = ?";
        int count = 0;

        // First get the count to create appropriate sized array
        try (PreparedStatement countStmt = connection.prepareStatement(sql)) {
            countStmt.setString(1, paymentMethodId);
            try (ResultSet countRs = countStmt.executeQuery()) {
                if (countRs.next()) {
                    count = countRs.getInt("count");
                }
            }
        }

        Transaction[] transactions = new Transaction[count];

        sql = "SELECT * FROM transaction WHERE payment_methodID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, paymentMethodId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int index = 0;
                while (resultSet.next()) {
                    transactions[index++] = mapResultSet(resultSet);
                }
            }
        }

        return transactions;
    }
}