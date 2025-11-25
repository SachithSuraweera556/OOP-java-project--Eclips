import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    JTextField userField;
    JPasswordField passField;
    JButton loginButton;

    public LoginFrame() {
        setTitle("Transport System Login");
        setSize(300, 200);
        setLayout(new GridLayout(3, 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userField = new JTextField();
        passField = new JPasswordField();
        loginButton = new JButton("Login");

        add(new JLabel("Username:"));
        add(userField);
        add(new JLabel("Password:"));
        add(passField);
        add(loginButton);

        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            
            // Uses AuthService for secure, hashed login
            if (AuthService.authenticate(user, pass)) {
                new ActivityFrame();
                dispose(); // Close login window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

// The Dashboard Class
class ActivityFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    JComboBox<String> activityCombo;
    JButton selectButton;
    JButton viewDetailsButton;
    JButton settingsButton;
    JButton reportButton; 

    public ActivityFrame() {
        // Display who is logged in
        String currentUser = (AuthService.currentUser != null) ? AuthService.currentUser : "Admin";
        setTitle("Dashboard - Logged in as: " + currentUser);
        
        setSize(650, 150); 
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20)); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Dropdown for standard activities
        activityCombo = new JComboBox<>(new String[]{
                "Add Owner", "Add Vehicle", "Add Employee", "Add Order", "Add Fuel"
        });

        // 2. Define Buttons
        selectButton = new JButton("Select Action");
        
        viewDetailsButton = new JButton("View Details");
        
        settingsButton = new JButton("Change Password");
        settingsButton.setBackground(new Color(255, 193, 7)); // Orange/Yellow
        settingsButton.setForeground(Color.BLACK);

        reportButton = new JButton("Monthly Report");
        reportButton.setBackground(new Color(40, 167, 69)); // Green
        reportButton.setForeground(Color.WHITE);

        // 3. Add Components to Frame
        add(new JLabel("Choose Activity:"));
        add(activityCombo);
        add(selectButton);
        add(viewDetailsButton);
        add(settingsButton);
        add(reportButton);

        // --- BUTTON ACTIONS ---

        // Action 1: Open 'Add' Forms
        selectButton.addActionListener(e -> {
            String activity = (String) activityCombo.getSelectedItem();
            switch (activity) {
                case "Add Owner" -> Owner.showOwnerForm();
                case "Add Vehicle" -> new AddVehicleFrame();
                case "Add Employee" -> new AddEmployeeFrame();
                case "Add Order" -> new AddOrderFrame();
                case "Add Fuel" -> new AddFuelFrame();
                default -> JOptionPane.showMessageDialog(this, "Feature not implemented yet");
            }
        });

        // Action 2: Open 'View' Frames
        viewDetailsButton.addActionListener(e -> {
            String activity = (String) activityCombo.getSelectedItem();
            switch (activity) {
                case "Add Owner" -> new ViewOwnerFrame();
                case "Add Vehicle" -> new ViewVehicleFrame();
                case "Add Employee" -> new ViewEmployeeFrame();
                case "Add Order" -> new ViewOrderFrame();
                case "Add Fuel" -> new ViewFuelFrame();
                default -> JOptionPane.showMessageDialog(this, "View not available for this item");
            }
        });

        // Action 3: Open Change Password
        settingsButton.addActionListener(e -> new ChangePasswordFrame());

        // Action 4: Open Monthly Report
        reportButton.addActionListener(e -> new MonthlyReportFrame());

        setLocationRelativeTo(null);
        setVisible(true);
    }
}