import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewOrderFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ViewOrderFrame() {
        setTitle("View All Orders");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create table with column names
        String[] columns = {"Order ID", "State", "Loaded Date", "Loaded Time", 
                           "Unloaded Date", "Unloaded Time", "From", "To"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // From
        table.getColumnModel().getColumn(7).setPreferredWidth(120); // To
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Add refresh button
        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadOrderData());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data when frame opens
        loadOrderData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadOrderData() {
        // Clear existing data
        tableModel.setRowCount(0);

        String sql = "SELECT OrderID, OrderState, LoadedDate, LoadedTime, " +
                     "UnloadedDate, UnloadedTime, LoadFrom, LoadTo " +
                     "FROM Orders ORDER BY OrderID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Add each row to the table
            while (rs.next()) {
                Object[] row = {
                    rs.getString("OrderID"),
                    rs.getString("OrderState"),
                    rs.getString("LoadedDate"),
                    rs.getString("LoadedTime"),
                    rs.getString("UnloadedDate"),
                    rs.getString("UnloadedTime"),
                    rs.getString("LoadFrom"),
                    rs.getString("LoadTo")
                };
                tableModel.addRow(row);
            }

            // Show message if no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No order records found in database.", 
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
        SwingUtilities.invokeLater(ViewOrderFrame::new);
    }
}