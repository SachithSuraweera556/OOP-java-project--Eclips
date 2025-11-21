import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewVehicleFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton, updateButton, deleteButton;

    public ViewVehicleFrame() {
        setTitle("View All Vehicles");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"Vehicle ID", "Vehicle Number", "Vehicle Type", "Model"};
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

        JPanel buttonPanel = new JPanel();
        refreshButton = new JButton("Refresh Data");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");
        
        updateButton.setBackground(new Color(70, 130, 180));
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        
        refreshButton.addActionListener(e -> loadVehicleData());
        updateButton.addActionListener(e -> updateSelectedVehicle());
        deleteButton.addActionListener(e -> deleteSelectedVehicle());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadVehicleData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadVehicleData() {
        tableModel.setRowCount(0);
        String sql = "SELECT VehicleID, VehicleNumber, VehicleType, Model FROM Vehicle ORDER BY VehicleID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("VehicleID"),
                    rs.getString("VehicleNumber"),
                    rs.getString("VehicleType"),
                    rs.getString("Model")
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No vehicle records found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelectedVehicle() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vehicleId = (int) tableModel.getValueAt(selectedRow, 0);
        String vehicleNumber = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete vehicle:\nID: " + vehicleId + "\nNumber: " + vehicleNumber + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Vehicle WHERE VehicleID = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, vehicleId);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadVehicleData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateSelectedVehicle() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int vehicleId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String currentType = (String) tableModel.getValueAt(selectedRow, 2);
        String currentModel = (String) tableModel.getValueAt(selectedRow, 3);
        
        JTextField numberField = new JTextField(currentNumber);
        JTextField typeField = new JTextField(currentType);
        JTextField modelField = new JTextField(currentModel);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Vehicle ID:"));
        panel.add(new JLabel(String.valueOf(vehicleId)));
        panel.add(new JLabel("Vehicle Number:"));
        panel.add(numberField);
        panel.add(new JLabel("Vehicle Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Vehicle (ID: " + vehicleId + ")",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newNumber = numberField.getText().trim();
            String newType = typeField.getText().trim();
            String newModel = modelField.getText().trim();
            
            if (newNumber.isEmpty() || newType.isEmpty() || newModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String sql = "UPDATE Vehicle SET VehicleNumber=?, VehicleType=?, Model=? WHERE VehicleID=?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, newNumber);
                ps.setString(2, newType);
                ps.setString(3, newModel);
                ps.setInt(4, vehicleId);
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Vehicle updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadVehicleData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewVehicleFrame::new);
    }
}