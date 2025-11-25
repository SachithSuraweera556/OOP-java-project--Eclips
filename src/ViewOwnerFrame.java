import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewOwnerFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton, updateButton, deleteButton, searchButton;
    private JTextField searchField;

    public ViewOwnerFrame() {
        setTitle("View All Owners");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create table
        String[] columns = {"Owner ID", "Name", "Phone", "Address", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Button panel at bottom
        JPanel buttonPanel = new JPanel();
        refreshButton = new JButton("Refresh Data");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");
        
        updateButton.setBackground(new Color(70, 130, 180));
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        
        refreshButton.addActionListener(e -> loadOwnerData());
        updateButton.addActionListener(e -> updateSelectedOwner());
        deleteButton.addActionListener(e -> deleteSelectedOwner());
        searchButton.addActionListener(e -> searchOwnerData());
        
        // Press Enter to search
        searchField.addActionListener(e -> searchOwnerData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadOwnerData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ============ SEARCH FUNCTION ============
    private void searchOwnerData() {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            loadOwnerData(); // If empty, show all
            return;
        }
        
        tableModel.setRowCount(0);
        
        // Search in all columns using LIKE with LOWER for case-insensitive
        String sql = "SELECT OwnerID, Name, Phone, Address, Email FROM Owner " +
                     "WHERE LOWER(CAST(OwnerID AS VARCHAR)) LIKE LOWER(?) " +
                     "OR LOWER(Name) LIKE LOWER(?) " +
                     "OR LOWER(Phone) LIKE LOWER(?) " +
                     "OR LOWER(Address) LIKE LOWER(?) " +
                     "OR LOWER(Email) LIKE LOWER(?) " +
                     "ORDER BY OwnerID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            
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
                    "No results found for: " + searchText, 
                    "Search Result", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Search error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadOwnerData() {
        searchField.setText(""); // Clear search field
        tableModel.setRowCount(0);
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
                JOptionPane.showMessageDialog(this, "No owner records found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelectedOwner() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int ownerId = (int) tableModel.getValueAt(selectedRow, 0);
        String ownerName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete owner:\nID: " + ownerId + "\nName: " + ownerName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Owner WHERE OwnerID = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, ownerId);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Owner deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOwnerData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete owner.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateSelectedOwner() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int ownerId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPhone = (String) tableModel.getValueAt(selectedRow, 2);
        String currentAddress = (String) tableModel.getValueAt(selectedRow, 3);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 4);
        
        JTextField nameField = new JTextField(currentName);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField addressField = new JTextField(currentAddress);
        JTextField emailField = new JTextField(currentEmail);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Owner ID:"));
        panel.add(new JLabel(String.valueOf(ownerId)));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Owner (ID: " + ownerId + ")",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newPhone = phoneField.getText().trim();
            String newAddress = addressField.getText().trim();
            String newEmail = emailField.getText().trim();
            
            if (newName.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
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
                    JOptionPane.showMessageDialog(this, "Owner updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOwnerData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update owner.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewOwnerFrame::new);
    }
}