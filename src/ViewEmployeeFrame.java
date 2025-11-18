import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewEmployeeFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ViewEmployeeFrame() {
        setTitle("View All Employees");
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create table with column names
        String[] columns = {"ID", "Name", "Age", "NIC", "Contact", "Address", 
                           "Email", "Account No", "Designation", "Salary", "Leaves", "Bonus"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        
        // Adjust column widths for better readability
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(50);  // Age
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Address
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Email
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Add refresh button
        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadEmployeeData());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data when frame opens
        loadEmployeeData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadEmployeeData() {
        // Clear existing data
        tableModel.setRowCount(0);

        String sql = "SELECT EmployeeID, Name, Age, NIC, Contact, Address, Email, " +
                     "AccountNumber, Designation, Salary, LeavesTaken, Bonus " +
                     "FROM Employee ORDER BY EmployeeID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Add each row to the table
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("EmployeeID"),
                    rs.getString("Name"),
                    rs.getInt("Age"),
                    rs.getString("NIC"),
                    rs.getString("Contact"),
                    rs.getString("Address"),
                    rs.getString("Email"),
                    rs.getString("AccountNumber"),
                    rs.getString("Designation"),
                    String.format("%.2f", rs.getDouble("Salary")),
                    rs.getInt("LeavesTaken"),
                    String.format("%.2f", rs.getDouble("Bonus"))
                };
                tableModel.addRow(row);
            }

            // Show message if no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No employee records found in database.", 
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewEmployeeFrame::new);
    }
}