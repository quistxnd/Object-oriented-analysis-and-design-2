package org.example.mapper;
import org.example.db.DatabaseManager;
import org.example.model.Goal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalMapper {
    public void insert(Goal goal) {
        String sql = "INSERT INTO goals (name, target_amount) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, goal.getName());
            pstmt.setDouble(2, goal.getTargetAmount());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Goal> findAll() {
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM goals")) {
            while (rs.next()) goals.add(new Goal(rs.getInt("id"), rs.getString("name"), rs.getDouble("target_amount")));
        } catch (SQLException e) { e.printStackTrace(); }
        return goals;
    }

    public void delete(int id) {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM goals WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}