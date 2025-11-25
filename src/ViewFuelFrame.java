import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewFuelFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton, updateButton, deleteButton, searchButton;
    private JTextField searchField;

    public ViewFuelFrame() {
        setTitle("View All Fuel Records");
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Date", "Vehicle No", "Tank Liters", "From", 
                           "To", "Distance (km)", "Fuel Needed (L)", "Receiver"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);
        
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
        
        refreshButton.addActionListener(e -> loadFuelData());
        updateButton.addActionListener(e -> updateSelectedFuel());
        deleteButton.addActionListener(e -> deleteSelectedFuel());
        searchButton.addActionListener(e -> searchFuelData());
        searchField.addActionListener(e -> searchFuelData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFuelData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ============ SEARCH FUNCTION ============
    private void searchFuelData() {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            loadFuelData();
            return;
        }
        
        tableModel.setRowCount(0);
        String sql = "SELECT id, date, vehicle_number, tank_liters, location_from, location_to, " +
                     "distance, fuel_needed, receiver_name FROM fuel_log " +
                     "WHERE LOWER(CAST(id AS VARCHAR)) LIKE LOWER(?) " +
                     "OR LOWER(vehicle_number) LIKE LOWER(?) " +
                     "OR LOWER(location_from) LIKE LOWER(?) " +
                     "OR LOWER(location_to) LIKE LOWER(?) " +
                     "OR LOWER(receiver_name) LIKE LOWER(?) " +
                     "ORDER BY id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchText + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, searchPattern);
            }
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getString("vehicle_number"),
                    rs.getDouble("tank_liters"),
                    rs.getString("location_from"),
                    rs.getString("location_to"),
                    rs.getDouble("distance"),
                    rs.getDouble("fuel_needed"),
                    rs.getString("receiver_name")
                };
                tableModel.addRow(row);
            }
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No results found for: " + searchText, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadFuelData() {
        searchField.setText("");
        tableModel.setRowCount(0);
        String sql = "SELECT id, date, vehicle_number, tank_liters, location_from, " +
                     "location_to, distance, fuel_needed, receiver_name FROM fuel_log ORDER BY id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getString("vehicle_number"),
                    rs.getDouble("tank_liters"),
                    rs.getString("location_from"),
                    rs.getString("location_to"),
                    rs.getDouble("distance"),
                    rs.getDouble("fuel_needed"),
                    rs.getString("receiver_name")
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No fuel records found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelectedFuel() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int fuelId = (int) tableModel.getValueAt(selectedRow, 0);
        String vehicleNo = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete fuel record:\nID: " + fuelId + "\nVehicle: " + vehicleNo + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM fuel_log WHERE id = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, fuelId);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Fuel record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadFuelData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete fuel record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateSelectedFuel() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int fuelId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentDate = (String) tableModel.getValueAt(selectedRow, 1);
        String currentVehicle = (String) tableModel.getValueAt(selectedRow, 2);
        double currentTank = (double) tableModel.getValueAt(selectedRow, 3);
        String currentFrom = (String) tableModel.getValueAt(selectedRow, 4);
        String currentTo = (String) tableModel.getValueAt(selectedRow, 5);
        double currentDistance = (double) tableModel.getValueAt(selectedRow, 6);
        double currentFuelNeeded = (double) tableModel.getValueAt(selectedRow, 7);
        String currentReceiver = (String) tableModel.getValueAt(selectedRow, 8);
        
        JTextField dateField = new JTextField(currentDate);
        JTextField vehicleField = new JTextField(currentVehicle);
        JTextField tankField = new JTextField(String.valueOf(currentTank));
        JTextField fromField = new JTextField(currentFrom);
        JTextField toField = new JTextField(currentTo);
        JTextField distanceField = new JTextField(String.valueOf(currentDistance));
        JTextField fuelNeededField = new JTextField(String.valueOf(currentFuelNeeded));
        JTextField receiverField = new JTextField(currentReceiver);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        panel.add(new JLabel("Fuel ID:")); panel.add(new JLabel(String.valueOf(fuelId)));
        panel.add(new JLabel("Date (yyyy-mm-dd):")); panel.add(dateField);
        panel.add(new JLabel("Vehicle Number:")); panel.add(vehicleField);
        panel.add(new JLabel("Tank Liters:")); panel.add(tankField);
        panel.add(new JLabel("From Location:")); panel.add(fromField);
        panel.add(new JLabel("To Location:")); panel.add(toField);
        panel.add(new JLabel("Distance (km):")); panel.add(distanceField);
        panel.add(new JLabel("Fuel Needed (L):")); panel.add(fuelNeededField);
        panel.add(new JLabel("Receiver Name:")); panel.add(receiverField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Fuel Record (ID: " + fuelId + ")",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newDate = dateField.getText().trim();
                String newVehicle = vehicleField.getText().trim();
                double newTank = Double.parseDouble(tankField.getText().trim());
                String newFrom = fromField.getText().trim();
                String newTo = toField.getText().trim();
                double newDistance = Double.parseDouble(distanceField.getText().trim());
                double newFuelNeeded = Double.parseDouble(fuelNeededField.getText().trim());
                String newReceiver = receiverField.getText().trim();
                
                if (newDate.isEmpty() || newVehicle.isEmpty() || newFrom.isEmpty() || 
                    newTo.isEmpty() || newReceiver.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String sql = "UPDATE fuel_log SET date=?, vehicle_number=?, tank_liters=?, " +
                             "location_from=?, location_to=?, distance=?, fuel_needed=?, receiver_name=? WHERE id=?";
                
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, newDate);
                    ps.setString(2, newVehicle);
                    ps.setDouble(3, newTank);
                    ps.setString(4, newFrom);
                    ps.setString(5, newTo);
                    ps.setDouble(6, newDistance);
                    ps.setDouble(7, newFuelNeeded);
                    ps.setString(8, newReceiver);
                    ps.setInt(9, fuelId);
                    
                    int rowsAffected = ps.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Fuel record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadFuelData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update fuel record.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Tank Liters, Distance, and Fuel Needed.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewFuelFrame::new);
    }
}