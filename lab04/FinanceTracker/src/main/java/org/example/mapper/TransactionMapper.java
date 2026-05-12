package org.example.mapper;

import org.example.db.DatabaseManager;
import org.example.model.Category;
import org.example.model.Transaction;
import org.example.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionMapper implements DataMapper<Transaction> {

    private final CategoryMapper categoryMapper = new CategoryMapper();
    private final UserMapper userMapper = new UserMapper(); // Добавили маппер юзеров!

    @Override
    public void insert(Transaction transaction) {
        String sql = "INSERT INTO transactions (amount, type, date, category_id, user_id, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setString(2, transaction.getType());
            pstmt.setString(3, transaction.getDate().toString());
            pstmt.setInt(4, transaction.getCategory().getId());
            pstmt.setInt(5, transaction.getUser().getId());
            pstmt.setString(6, transaction.getDescription()); // 6-й параметр — описание

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Transaction findById(int id) { return null; }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                String desc = rs.getString("description"); 

                Category category = categoryMapper.findById(rs.getInt("category_id"));
                User user = userMapper.findById(rs.getInt("user_id"));

                
                transactions.add(new Transaction(id, amount, type, date, category, user, desc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
