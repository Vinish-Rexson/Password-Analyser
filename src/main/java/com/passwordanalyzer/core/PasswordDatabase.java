package com.passwordanalyzer.core;

import com.passwordanalyzer.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordDatabase {
    
    public void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS common_passwords (
                id SERIAL PRIMARY KEY,
                password_hash VARCHAR(255) NOT NULL UNIQUE,
                frequency INT DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            // Initialize with common breached passwords
            DatabaseConnection.initializeBreachedPasswords();
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

    public List<String[]> getBreachedPasswords() {
        List<String[]> breachedPasswords = new ArrayList<>();
        String sql = """
            SELECT id, password_hash, frequency, created_at 
            FROM common_passwords 
            ORDER BY created_at DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] row = new String[4];
                row[0] = String.valueOf(rs.getInt("id"));
                row[1] = rs.getString("password_hash");
                row[2] = String.valueOf(rs.getInt("frequency"));
                row[3] = rs.getTimestamp("created_at").toString();
                breachedPasswords.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return breachedPasswords;
    }
} 