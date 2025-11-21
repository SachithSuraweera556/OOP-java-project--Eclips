import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewOrderFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton, updateButton, deleteButton;

    public ViewOrderFrame() {
        setTitle("View All Orders");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"Order ID", "State", "Loaded Date", "Loaded Time", 
                           "Unloaded Date", "Unloaded Time", "From", "To"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);
        
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
        
        refreshButton.addActionListener(e -> loadOrderData());
        updateButton.addActionListener(e -> updateSelectedOrder());
        deleteButton.addActionListener(e -> deleteSelectedOrder());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadOrderData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadOrderData() {
        tableModel.setRowCount(0);
        String sql = "SELECT OrderID, OrderState, LoadedDate, LoadedTime, " +
                     "UnloadedDate, UnloadedTime, LoadFrom, LoadTo FROM Orders ORDER BY OrderID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No order records found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelectedOrder() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        String orderState = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete order:\nID: " + orderId + "\nState: " + orderState + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Orders WHERE OrderID = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, orderId);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Order deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOrderData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete order.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateSelectedOrder() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentState = (String) tableModel.getValueAt(selectedRow, 1);
        String currentLoadedDate = (String) tableModel.getValueAt(selectedRow, 2);
        String currentLoadedTime = (String) tableModel.getValueAt(selectedRow, 3);
        String currentUnloadedDate = (String) tableModel.getValueAt(selectedRow, 4);
        String currentUnloadedTime = (String) tableModel.getValueAt(selectedRow, 5);
        String currentFrom = (String) tableModel.getValueAt(selectedRow, 6);
        String currentTo = (String) tableModel.getValueAt(selectedRow, 7);
        
        JTextField stateField = new JTextField(currentState);
        JTextField loadedDateField = new JTextField(currentLoadedDate);
        JTextField loadedTimeField = new JTextField(currentLoadedTime);
        JTextField unloadedDateField = new JTextField(currentUnloadedDate);
        JTextField unloadedTimeField = new JTextField(currentUnloadedTime);
        JTextField fromField = new JTextField(currentFrom);
        JTextField toField = new JTextField(currentTo);
        
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        panel.add(new JLabel("Order ID:")); panel.add(new JLabel(orderId));
        panel.add(new JLabel("Order State:")); panel.add(stateField);
        panel.add(new JLabel("Loaded Date:")); panel.add(loadedDateField);
        panel.add(new JLabel("Loaded Time:")); panel.add(loadedTimeField);
        panel.add(new JLabel("Unloaded Date:")); panel.add(unloadedDateField);
        panel.add(new JLabel("Unloaded Time:")); panel.add(unloadedTimeField);
        panel.add(new JLabel("From:")); panel.add(fromField);
        panel.add(new JLabel("To:")); panel.add(toField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Order (ID: " + orderId + ")",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newState = stateField.getText().trim();
            String newLoadedDate = loadedDateField.getText().trim();
            String newLoadedTime = loadedTimeField.getText().trim();
            String newUnloadedDate = unloadedDateField.getText().trim();
            String newUnloadedTime = unloadedTimeField.getText().trim();
            String newFrom = fromField.getText().trim();
            String newTo = toField.getText().trim();
            
            if (newState.isEmpty() || newLoadedDate.isEmpty() || newLoadedTime.isEmpty() ||
                newUnloadedDate.isEmpty() || newUnloadedTime.isEmpty() || newFrom.isEmpty() || newTo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String sql = "UPDATE Orders SET OrderState=?, LoadedDate=?, LoadedTime=?, " +
                         "UnloadedDate=?, UnloadedTime=?, LoadFrom=?, LoadTo=? WHERE OrderID=?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, newState);
                ps.setString(2, newLoadedDate);
                ps.setString(3, newLoadedTime);
                ps.setString(4, newUnloadedDate);
                ps.setString(5, newUnloadedTime);
                ps.setString(6, newFrom);
                ps.setString(7, newTo);
                ps.setString(8, orderId);
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Order updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOrderData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update order.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewOrderFrame::new);
    }
}