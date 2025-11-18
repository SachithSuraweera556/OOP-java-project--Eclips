import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewOwnerFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ViewOwnerFrame() {
        setTitle("View All Owners");
        setSize(800, 400);
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
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Add refresh button at the bottom
        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadOwnerData());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data when frame opens
        loadOwnerData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadOwnerData() {
        // Clear existing data
        tableModel.setRowCount(0);

        String sql = "SELECT OwnerID, Name, Phone, Address, Email FROM Owner ORDER BY OwnerID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Add each row to the table
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

            // Show message if no data found
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

    // Test method - you can remove this later
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewOwnerFrame::new);
    }
}