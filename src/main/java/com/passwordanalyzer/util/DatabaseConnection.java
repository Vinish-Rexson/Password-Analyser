package com.passwordanalyzer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String NEON_DB_URL = "jdbc:postgresql://ep-summer-mountain-a83d27zp-pooler.eastus2.azure.neon.tech/neondb";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_yYG7Ij6RPJfV";

    static {
        try {
            // Explicitly load the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver loaded successfully");
            
            // Create table and initialize data on startup
            createTableAndInitializeData();
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found!");
            e.printStackTrace();
        }
    }

    private static void createTableAndInitializeData() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS common_passwords (
                id SERIAL PRIMARY KEY,
                password_hash VARCHAR(255) NOT NULL UNIQUE,
                frequency INT DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            System.out.println("Creating table if not exists...");
            stmt.execute(createTableSQL);
            System.out.println("Table created successfully");
            
            // Initialize with common breached passwords
            initializeBreachedPasswords();
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver loaded successfully");
            
            // Create connection with separate parameters
            Connection conn = DriverManager.getConnection(NEON_DB_URL, USER, PASSWORD);
            System.out.println("Database connection established successfully");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found!");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    public static void initializeBreachedPasswords() {
        String[] commonBreachedPasswords = {
            // Most common passwords from data breaches
            "123456", "123456789", "qwerty", "password", "12345", "qwerty123",
            "1q2w3e", "12345678", "111111", "1234567890", "1234567", "password1",
            "abc123", "password123", "1234", "admin", "letmein", "welcome",
            "monkey123", "football", "superman", "dragon", "master", "hello",
            "freedom", "whatever", "qazwsx", "trustno1", "baseball", "batman",
            // Common patterns
            "abcd1234", "a1b2c3d4", "password!", "admin123", "login123",
            // Years
            "2024", "2023", "2022", "2021",
            // Common names with numbers
            "michael123", "jennifer123", "thomas123", "jordan23",
            // Keyboard patterns
            "qwertyuiop", "asdfghjkl", "zxcvbnm", "1qaz2wsx"
        };

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO common_passwords (password_hash, frequency) VALUES (?, 1) ON CONFLICT (password_hash) DO UPDATE SET frequency = common_passwords.frequency + 1")) {
            
            System.out.println("Starting to insert " + commonBreachedPasswords.length + " passwords...");
            int count = 0;
            
            for (String password : commonBreachedPasswords) {
                String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
                pstmt.setString(1, hashedPassword);
                pstmt.executeUpdate();
                count++;
                if (count % 10 == 0) {
                    System.out.println("Inserted " + count + " passwords so far...");
                }
            }
            
            System.out.println("Successfully initialized breached passwords database with " + count + " passwords");
        } catch (SQLException e) {
            System.err.println("Error initializing breached passwords: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to manually trigger initialization if needed
    public static void forceInitialization() {
        System.out.println("Forcing database initialization...");
        createTableAndInitializeData();
    }

    // Test connection method
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Successfully connected to the database!");
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 