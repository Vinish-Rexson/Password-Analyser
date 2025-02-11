package com.passwordanalyzer.core;

import com.passwordanalyzer.util.DatabaseConnection;
import java.sql.*;

public class PasswordDatabase {
    
    public void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS common_passwords (
                id SERIAL PRIMARY KEY,
                password_hash VARCHAR(255) NOT NULL,
                frequency INT DEFAULT 1
            )
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPasswordCompromised(String password) {
        String sql = "SELECT COUNT(*) FROM common_passwords WHERE password_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addCompromisedPassword(String password) {
        String sql = "INSERT INTO common_passwords (password_hash) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 