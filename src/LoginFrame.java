import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    JTextField userField;
    JPasswordField passField;
    JButton loginButton;

    public LoginFrame() {
        setTitle("Transport System Login");
        setSize(750, 450); // Larger size for the split layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(null); // Using Absolute Layout for precise custom design
        setResizable(false);

        // --- LEFT PANEL (Branding) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(44, 62, 80)); // Dark Blue
        leftPanel.setBounds(0, 0, 300, 450);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Icon
        JLabel iconLabel = new JLabel("🚛", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Brand Title
        JLabel titleLabel = new JLabel("<html><center>TRANSPORT<br>MANAGER</center></html>", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline
        JLabel tagLabel = new JLabel("Secure & Reliable", JLabel.CENTER);
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagLabel.setForeground(new Color(189, 195, 199));
        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to Left Panel with Spacing
        leftPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        leftPanel.add(iconLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(tagLabel);

        add(leftPanel);

        // --- RIGHT PANEL (Login Form) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBounds(300, 0, 450, 450);
        rightPanel.setLayout(null);

        // Heading
        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loginTitle.setForeground(new Color(44, 62, 80));
        loginTitle.setBounds(40, 50, 300, 40);
        rightPanel.add(loginTitle);

        JLabel subTitle = new JLabel("Please login to your account");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        subTitle.setBounds(40, 95, 300, 20);
        rightPanel.add(subTitle);

        // Username Field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(new Color(44, 62, 80));
        userLabel.setBounds(40, 150, 100, 20);
        rightPanel.add(userLabel);

        userField = new JTextField();
        userField.setBounds(40, 175, 350, 30);
        userField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(44, 62, 80))); // Bottom border only
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(userField);

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(44, 62, 80));
        passLabel.setBounds(40, 230, 100, 20);
        rightPanel.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(40, 255, 350, 30);
        passField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(44, 62, 80)));
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(passField);

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(40, 330, 350, 45);
        loginButton.setBackground(new Color(52, 152, 219)); // Bright Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add Hover Effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(41, 128, 185)); // Darker Blue on Hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(52, 152, 219)); // Restore Color
            }
        });

        rightPanel.add(loginButton);
        add(rightPanel);

        // --- Logic ---
        loginButton.addActionListener(e -> performLogin());
        
        // Allow pressing "Enter" key to login
        getRootPane().setDefaultButton(loginButton); 

        setVisible(true);
    }

    private void performLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        
        if (AuthService.authenticate(user, pass)) {
            new ActivityFrame();
            dispose(); // Close login window
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Apply Nimbus Look and Feel for other frames
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

// ==========================================
//   MODERN DASHBOARD (ActivityFrame)
// ==========================================
class ActivityFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public ActivityFrame() {
        String currentUser = (AuthService.currentUser != null) ? AuthService.currentUser : "Admin";
        setTitle("Dashboard - Transport Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR (Navigation) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80)); // Dark Blue
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        // User Profile Area
        JLabel userIcon = new JLabel("👤", JLabel.CENTER);
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        userIcon.setForeground(Color.WHITE);
        userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel welcomeLabel = new JLabel("Hello, " + currentUser);
        welcomeLabel.setForeground(Color.LIGHT_GRAY);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(userIcon);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(welcomeLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Add Sidebar Buttons
        // --- MODIFIED: REMOVED CHANGE PASSWORD BUTTON ---
        
        sidebar.add(createSidebarButton("  Logout", e -> {
            dispose();
            new LoginFrame();
        }));

        add(sidebar, BorderLayout.WEST);

        // --- 2. MAIN CONTENT AREA ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(236, 240, 241)); // Light Gray

        // 2.1 Top Info Widgets (The "Info at a Glance")
        JPanel widgetPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        widgetPanel.setBackground(new Color(236, 240, 241));
        widgetPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Fetch real data using ReportService
        int empCount = ReportService.getTotalCount("Employee");
        int vehCount = ReportService.getTotalCount("Vehicle");
        int ordCount = ReportService.getTotalOrders();
        int ownCount = ReportService.getTotalCount("Owner");

        widgetPanel.add(createWidget("Employees", empCount, new Color(52, 152, 219))); // Blue
        widgetPanel.add(createWidget("Vehicles", vehCount, new Color(230, 126, 34)));  // Orange
        widgetPanel.add(createWidget("Total Orders", ordCount, new Color(46, 204, 113))); // Green
        widgetPanel.add(createWidget("Owners", ownCount, new Color(155, 89, 182)));    // Purple

        contentPanel.add(widgetPanel, BorderLayout.NORTH);

        // 2.2 Action Grid (The Big Buttons)
        JPanel actionGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        actionGrid.setBackground(new Color(236, 240, 241));
        actionGrid.setBorder(new EmptyBorder(0, 30, 30, 30));

        // Owners
        actionGrid.add(createActionButton("Manage Owners", "🤝", new Color(52, 73, 94), 
            e -> showActionDialog("Owner", Owner::showOwnerForm, ViewOwnerFrame::new)));
        
        // Vehicles
        actionGrid.add(createActionButton("Manage Vehicles", "🚛", new Color(52, 73, 94), 
            e -> showActionDialog("Vehicle", AddVehicleFrame::new, ViewVehicleFrame::new)));
        
        // Employees
        actionGrid.add(createActionButton("Manage Employees", "👷", new Color(52, 73, 94), 
            e -> showActionDialog("Employee", AddEmployeeFrame::new, ViewEmployeeFrame::new)));
        
        // Orders
        actionGrid.add(createActionButton("Manage Orders", "📦", new Color(52, 73, 94), 
            e -> showActionDialog("Order", AddOrderFrame::new, ViewOrderFrame::new)));
        
        // Fuel
        actionGrid.add(createActionButton("Fuel Logs", "⛽", new Color(52, 73, 94), 
            e -> showActionDialog("Fuel Log", AddFuelFrame::new, ViewFuelFrame::new)));
        
        // Reports
        actionGrid.add(createActionButton("Monthly Reports", "📊", new Color(39, 174, 96), 
            e -> new MonthlyReportFrame()));

        contentPanel.add(actionGrid, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- HELPER METHODS FOR UI ---

    private JPanel createWidget(String title, int count, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setForeground(Color.WHITE);
        
        JLabel textLabel = new JLabel(title);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textLabel.setForeground(new Color(240, 240, 240));
        
        panel.add(countLabel, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createActionButton(String text, String icon, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton("<html><center><span style='font-size:30px'>" + icon + "</span><br>" + text + "</center></html>");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        btn.addActionListener(action);
        return btn;
    }

    private JButton createSidebarButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(250, 40));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(action);
        return btn;
    }

    private void showActionDialog(String entityName, Runnable addAction, Runnable viewAction) {
        Object[] options = {"Add New " + entityName, "View All " + entityName + "s", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select an action for " + entityName + ":",
                "Manage " + entityName,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) addAction.run();
        else if (choice == 1) viewAction.run();
    }
}