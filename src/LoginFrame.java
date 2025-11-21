import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    /**
	 * 
	 */
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
            if (user.equals("admin") && pass.equals("admin")) { // simple login
                new ActivityFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

class ActivityFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox<String> activityCombo;
    JButton selectButton;
    JButton viewDetailsButton; // NEW: View Details button

    public ActivityFrame() {
        setTitle("Select Activity");
        setSize(500, 150);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ComboBox with all activities
        activityCombo = new JComboBox<>(new String[]{
                "Add Owner", "Add Vehicle", "Add Employee", "Add Order", "Add Fuel"
        });

        selectButton = new JButton("Select");
        viewDetailsButton = new JButton("View Details"); // NEW: Create View Details button

        add(new JLabel("Choose Action:"));
        add(activityCombo);
        add(selectButton);
        add(viewDetailsButton); // NEW: Add the button to the frame

        // Select button action - Opens Add forms
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

        // NEW: View Details button action - Opens View frames
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

        setLocationRelativeTo(null);
        setVisible(true);
    }
}