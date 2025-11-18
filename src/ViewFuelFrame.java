import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewFuelFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ViewFuelFrame() {
        setTitle("View All Fuel Records");
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create table with column names
        String[] columns = {"ID", "Date", "Vehicle No", "Tank Liters", "From", 
                           "To", "Distance (km)", "Fuel Needed (L)", "Receiver"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Vehicle No
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // From
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // To
        table.getColumnModel().getColumn(8).setPreferredWidth(120); // Receiver
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Add refresh button
        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadFuelData());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data when frame opens
        loadFuelData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadFuelData() {
        // Clear existing data
        tableModel.setRowCount(0);

        String sql = "SELECT id, date, vehicle_number, tank_liters, location_from, " +
                     "location_to, distance, fuel_needed, receiver_name " +
                     "FROM fuel_log ORDER BY id DESC"; // Show newest first
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Add each row to the table
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getString("vehicle_number"),
                    String.format("%.2f", rs.getDouble("tank_liters")),
                    rs.getString("location_from"),
                    rs.getString("location_to"),
                    String.format("%.2f", rs.getDouble("distance")),
                    String.format("%.2f", rs.getDouble("fuel_needed")),
                    rs.getString("receiver_name")
                };
                tableModel.addRow(row);
            }

            // Show message if no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No fuel records found in database.", 
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
        SwingUtilities.invokeLater(ViewFuelFrame::new);
    }
}