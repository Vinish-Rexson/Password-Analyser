package com.passwordanalyzer.ui;

import com.passwordanalyzer.core.PasswordGenerator;
import com.passwordanalyzer.core.PasswordStrengthAnalyzer;
import com.passwordanalyzer.core.PasswordDatabase;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.passwordanalyzer.util.DatabaseConnection;

public class PasswordAnalyzerUI extends JFrame {
    private JPasswordField passwordField;
    private JProgressBar strengthMeter;
    private JLabel strengthLabel;
    private JTextArea suggestionsArea;
    private JButton generateButton;
    private JButton analyzeButton;
    private JButton toggleVisibilityButton;
    private JButton checkBreachButton;
    private JButton viewBreachedButton;
    private JLabel scoreLabel;
    private final PasswordStrengthAnalyzer analyzer;
    private final PasswordDatabase passwordDB;
    private boolean passwordVisible = false;

    public PasswordAnalyzerUI() {
        analyzer = new PasswordStrengthAnalyzer();
        passwordDB = new PasswordDatabase();
        passwordDB.initializeDatabase();
        DatabaseConnection.forceInitialization();
        setupUI();
    }

    private void setupUI() {
        setTitle("Password Strength Analyzer & Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
        
        // Set custom look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        layoutComponents();
        addListeners();
        
        // Set initial state
        updateStrengthIndicator("");
    }

    private void initializeComponents() {
        // Initialize with modern styling
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        strengthMeter = new JProgressBar(0, 100);
        strengthMeter.setPreferredSize(new Dimension(200, 20));
        strengthMeter.setStringPainted(true);
        
        strengthLabel = new JLabel("Password Strength: ");
        strengthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        scoreLabel = new JLabel("0%");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        suggestionsArea = new JTextArea(5, 30);
        suggestionsArea.setEditable(false);
        suggestionsArea.setWrapStyleWord(true);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        suggestionsArea.setBackground(new Color(245, 245, 245));
        
        generateButton = createStyledButton("Generate Password", new Color(0, 120, 212));
        analyzeButton = createStyledButton("Analyze Password", new Color(0, 153, 51));
        toggleVisibilityButton = createStyledButton("Show", new Color(100, 100, 100));
        checkBreachButton = createStyledButton("Check for Breaches", new Color(220, 53, 69));
        viewBreachedButton = createStyledButton("View Breached Passwords Database", new Color(128, 0, 128));
        viewBreachedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Password input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Enter Password: "));
        inputPanel.add(passwordField);
        inputPanel.add(toggleVisibilityButton);

        // Strength indicator panel
        JPanel strengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        strengthPanel.add(strengthLabel);
        strengthPanel.add(strengthMeter);
        strengthPanel.add(scoreLabel);

        // Suggestions panel
        JPanel suggestionsPanel = new JPanel(new BorderLayout());
        suggestionsPanel.setBorder(BorderFactory.createTitledBorder("Suggestions"));
        suggestionsPanel.add(new JScrollPane(suggestionsArea), BorderLayout.CENTER);

        // Main buttons panel
        JPanel mainButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        mainButtonPanel.add(analyzeButton);
        mainButtonPanel.add(generateButton);
        mainButtonPanel.add(checkBreachButton);

        // Separate panel for View Breached DB button
        JPanel breachDBPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        breachDBPanel.setBorder(BorderFactory.createTitledBorder("Database Operations"));
        viewBreachedButton.setPreferredSize(new Dimension(200, 35)); // Make button bigger
        breachDBPanel.add(viewBreachedButton);

        // Add all panels to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(strengthPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(suggestionsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(mainButtonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(breachDBPanel);  // Add the new panel

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { analyzeCurrentPassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { analyzeCurrentPassword(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { analyzeCurrentPassword(); }
        });

        analyzeButton.addActionListener(e -> analyzeCurrentPassword());
        generateButton.addActionListener(e -> showPasswordGeneratorDialog());
        toggleVisibilityButton.addActionListener(e -> togglePasswordVisibility());
        checkBreachButton.addActionListener(e -> checkForBreaches());
        viewBreachedButton.addActionListener(e -> showBreachedPasswords());
        
        // Add hover effect
        viewBreachedButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                viewBreachedButton.setBackground(new Color(160, 32, 240)); // Lighter purple when hovered
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                viewBreachedButton.setBackground(new Color(128, 0, 128)); // Back to original purple
            }
        });
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
            toggleVisibilityButton.setText("Hide");
        } else {
            passwordField.setEchoChar('•');
            toggleVisibilityButton.setText("Show");
        }
    }

    private void analyzeCurrentPassword() {
        String password = new String(passwordField.getPassword());
        PasswordStrengthAnalyzer.PasswordStrength strength = analyzer.analyzePassword(password);
        updateStrengthIndicator(password);
        updateSuggestions(strength.getSuggestions());
    }

    private void updateStrengthIndicator(String password) {
        if (password.isEmpty()) {
            strengthMeter.setValue(0);
            strengthMeter.setString("Empty");
            scoreLabel.setText("0%");
            strengthMeter.setForeground(Color.GRAY);
            return;
        }

        PasswordStrengthAnalyzer.PasswordStrength strength = analyzer.analyzePassword(password);
        int score = strength.getScore();
        strengthMeter.setValue(score);
        scoreLabel.setText(score + "%");

        // Update color based on strength
        if (score < 40) {
            strengthMeter.setForeground(new Color(255, 69, 58));
        } else if (score < 70) {
            strengthMeter.setForeground(new Color(255, 159, 10));
        } else {
            strengthMeter.setForeground(new Color(48, 209, 88));
        }
    }

    private void updateSuggestions(java.util.List<String> suggestions) {
        StringBuilder sb = new StringBuilder();
        for (String suggestion : suggestions) {
            sb.append("• ").append(suggestion).append("\n");
        }
        suggestionsArea.setText(sb.toString());
    }

    private void showPasswordGeneratorDialog() {
        JDialog dialog = new JDialog(this, "Generate Password", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Length panel
        JPanel lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 32, 1));
        lengthPanel.add(new JLabel("Password Length:"));
        lengthPanel.add(lengthSpinner);

        // Options panel
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        JCheckBox lowerCase = new JCheckBox("Lowercase (a-z)", true);
        JCheckBox upperCase = new JCheckBox("Uppercase (A-Z)", true);
        JCheckBox digits = new JCheckBox("Digits (0-9)", true);
        JCheckBox special = new JCheckBox("Special (!@#$%)", true);

        optionsPanel.add(lowerCase);
        optionsPanel.add(upperCase);
        optionsPanel.add(digits);
        optionsPanel.add(special);

        // Generate button
        JButton generateBtn = createStyledButton("Generate", new Color(0, 120, 212));
        generateBtn.addActionListener(e -> {
            PasswordGenerator generator = new PasswordGenerator();
            String generatedPassword = generator.generatePassword(
                (Integer) lengthSpinner.getValue(),
                lowerCase.isSelected(),
                upperCase.isSelected(),
                digits.isSelected(),
                special.isSelected()
            );
            passwordField.setText(generatedPassword);
            dialog.dispose();
            analyzeCurrentPassword();
        });

        contentPanel.add(lengthPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(optionsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(generateBtn);

        dialog.add(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void checkForBreaches() {
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a password to check for breaches.",
                "No Password",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        checkBreachButton.setEnabled(false);

        // Run the check in a background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return passwordDB.isPasswordCompromised(password);
            }

            @Override
            protected void done() {
                try {
                    boolean isCompromised = get();
                    String message;
                    int messageType;
                    
                    if (isCompromised) {
                        message = "⚠️ WARNING: This password has been found in data breaches!\n" +
                                "It is strongly recommended to choose a different password.";
                        messageType = JOptionPane.ERROR_MESSAGE;
                    } else {
                        message = "✅ Good news! This password hasn't been found in known data breaches.";
                        messageType = JOptionPane.INFORMATION_MESSAGE;
                    }

                    JOptionPane.showMessageDialog(PasswordAnalyzerUI.this,
                        message,
                        "Breach Check Result",
                        messageType);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PasswordAnalyzerUI.this,
                        "Error checking for breaches. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Restore cursor and button state
                    setCursor(Cursor.getDefaultCursor());
                    checkBreachButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void showBreachedPasswords() {
        JDialog dialog = new JDialog(this, "Breached Passwords Database", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Create table model
        String[] columnNames = {"ID", "Password Hash", "Frequency", "Date Added"};
        List<String[]> breachedData = passwordDB.getBreachedPasswords();
        
        Object[][] data = breachedData.toArray(new Object[0][]);
        
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(300); // Hash
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Frequency
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Date
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Total Breached Passwords: " + breachedData.size()));

        JButton closeButton = createStyledButton("Close", new Color(100, 100, 100));
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PasswordAnalyzerUI().setVisible(true);
        });
    }
} 