import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewOwnerFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton, updateButton, deleteButton;

    public ViewOwnerFrame() {
        setTitle("View All Owners");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create table with column names
        String[] columns = {"Owner ID", "Name", "Phone", "Address", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row selection
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel with all three buttons
        JPanel buttonPanel = new JPanel();
        
        refreshButton = new JButton("Refresh Data");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");
        
        // Set button colors for better visibility
        updateButton.setBackground(new Color(70, 130, 180)); // Steel blue
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 53, 69)); // Red
        deleteButton.setForeground(Color.WHITE);
        
        // Add action listeners
        refreshButton.addActionListener(e -> loadOwnerData());
        updateButton.addActionListener(e -> updateSelectedOwner());
        deleteButton.addActionListener(e -> deleteSelectedOwner());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data when frame opens
        loadOwnerData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ============ LOAD DATA ============
    private void loadOwnerData() {
        tableModel.setRowCount(0); // Clear existing data

        String sql = "SELECT OwnerID, Name, Phone, Address, Email FROM Owner ORDER BY OwnerID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("OwnerID"),
                    rs.getString("Name"),
                    rs.getString("Phone"),
                    rs.getString("Address"),
                    rs.getString("Email")
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No owner records found in database.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ============ DELETE FUNCTION ============
    private void deleteSelectedOwner() {
        int selectedRow = table.getSelectedRow();
        
        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a row to delete!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the Owner ID from selected row (column 0)
        int ownerId = (int) tableModel.getValueAt(selectedRow, 0);
        String ownerName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete owner:\n" +
            "ID: " + ownerId + "\nName: " + ownerName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete from database
            String sql = "DELETE FROM Owner WHERE OwnerID = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, ownerId);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Owner deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadOwnerData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete owner. Record may not exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // ============ UPDATE FUNCTION ============
    private void updateSelectedOwner() {
        int selectedRow = table.getSelectedRow();
        
        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a row to update!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get current values from selected row
        int ownerId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPhone = (String) tableModel.getValueAt(selectedRow, 2);
        String currentAddress = (String) tableModel.getValueAt(selectedRow, 3);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 4);
        
        // Create input fields with current values
        JTextField nameField = new JTextField(currentName);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField addressField = new JTextField(currentAddress);
        JTextField emailField = new JTextField(currentEmail);
        
        // Create panel for the dialog
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Owner ID:"));
        panel.add(new JLabel(String.valueOf(ownerId))); // ID is not editable
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        
        // Show dialog
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Update Owner (ID: " + ownerId + ")",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            // Get new values
            String newName = nameField.getText().trim();
            String newPhone = phoneField.getText().trim();
            String newAddress = addressField.getText().trim();
            String newEmail = emailField.getText().trim();
            
            // Validate - check if fields are empty
            if (newName.isEmpty() || newPhone.isEmpty() || 
                newAddress.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "All fields are required!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update database
            String sql = "UPDATE Owner SET Name=?, Phone=?, Address=?, Email=? WHERE OwnerID=?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, newName);
                ps.setString(2, newPhone);
                ps.setString(3, newAddress);
                ps.setString(4, newEmail);
                ps.setInt(5, ownerId);
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Owner updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadOwnerData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to update owner.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewOwnerFrame::new);
    }
}