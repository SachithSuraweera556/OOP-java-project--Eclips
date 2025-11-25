import javax.swing.*;
import java.awt.*;

public class ChangePasswordFrame extends JFrame {
    private JPasswordField currentPassField, newPassField, confirmPassField;
    private JButton updateButton;

    public ChangePasswordFrame() {
        setTitle("Change Password");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        // UI Components
        add(new JLabel("  Current Password:"));
        currentPassField = new JPasswordField();
        add(currentPassField);

        add(new JLabel("  New Password:"));
        newPassField = new JPasswordField();
        add(newPassField);

        add(new JLabel("  Confirm New Password:"));
        confirmPassField = new JPasswordField();
        add(confirmPassField);

        add(new JLabel("")); // Empty placeholder
        updateButton = new JButton("Update Password");
        add(updateButton);

        // Action Listener
        updateButton.addActionListener(e -> updatePassword());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updatePassword() {
        String currentPass = new String(currentPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        // 1. Validation: Check if fields are empty
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validation: Check if new passwords match
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Security Check: Verify old password first
        if (AuthService.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Session expired. Please login again.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Verify the user knows their current password before allowing a change
        boolean isVerified = AuthService.authenticate(AuthService.currentUser, currentPass);
        
        if (isVerified) {
            // 4. Update Password
            boolean success = AuthService.changePassword(AuthService.currentUser, newPass);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Security Warning", JOptionPane.ERROR_MESSAGE);
        }
    }
}