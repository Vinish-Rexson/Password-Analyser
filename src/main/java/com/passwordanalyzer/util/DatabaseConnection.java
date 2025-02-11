package com.passwordanalyzer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://ep-aged-bird-a5rksgs4-pooler.us-east-2.aws.neon.tech/Sample";
    private static final String USER = "Sample_owner";
    private static final String PASSWORD = "mQZjgkHRf3b6";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
} 