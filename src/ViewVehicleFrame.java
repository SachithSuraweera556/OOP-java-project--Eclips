import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewVehicleFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ViewVehicleFrame() {
        setTitle("View All Vehicles");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create table with column names
        String[] columns = {"Vehicle ID", "Vehicle Number", "Vehicle Type", "Model"};
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

        // Add refresh button
        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadVehicleData());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data when frame opens
        loadVehicleData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadVehicleData() {
        // Clear existing data
        tableModel.setRowCount(0);

        String sql = "SELECT VehicleID, VehicleNumber, VehicleType, Model FROM Vehicle ORDER BY VehicleID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Add each row to the table
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("VehicleID"),
                    rs.getString("VehicleNumber"),
                    rs.getString("VehicleType"),
                    rs.getString("Model")
                };
                tableModel.addRow(row);
            }

            // Show message if no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No vehicle records found in database.", 
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
        SwingUtilities.invokeLater(ViewVehicleFrame::new);
    }
}