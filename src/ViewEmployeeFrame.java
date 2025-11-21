import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewEmployeeFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
    private DefaultTableModel tableModel;
    private JButton refreshButton, updateButton, deleteButton;

    public ViewEmployeeFrame() {
        setTitle("View All Employees");
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Name", "Age", "NIC", "Contact", "Address", 
                           "Email", "Account No", "Designation", "Salary", "Leaves", "Bonus"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
        
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
        
        refreshButton.addActionListener(e -> loadEmployeeData());
        updateButton.addActionListener(e -> updateSelectedEmployee());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadEmployeeData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        String sql = "SELECT EmployeeID, Name, Age, NIC, Contact, Address, Email, " +
                     "AccountNumber, Designation, Salary, LeavesTaken, Bonus FROM Employee ORDER BY EmployeeID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No employee records found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int empId = (int) tableModel.getValueAt(selectedRow, 0);
        String empName = (String) tableModel.getValueAt(selectedRow, 1);
        String designation = (String) tableModel.getValueAt(selectedRow, 8);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee:\nID: " + empId + "\nName: " + empName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                // Delete from role-specific table first
                String roleTable = designation; // Driver, Officer, or Helper
                String sqlRole = "DELETE FROM " + roleTable + " WHERE EmployeeID = ?";
                PreparedStatement psRole = conn.prepareStatement(sqlRole);
                psRole.setInt(1, empId);
                psRole.executeUpdate();
                
                // Delete from Employee table
                String sqlEmp = "DELETE FROM Employee WHERE EmployeeID = ?";
                PreparedStatement psEmp = conn.prepareStatement(sqlEmp);
                psEmp.setInt(1, empId);
                int rowsAffected = psEmp.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateSelectedEmployee() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int empId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        int currentAge = (int) tableModel.getValueAt(selectedRow, 2);
        String currentNIC = (String) tableModel.getValueAt(selectedRow, 3);
        String currentContact = (String) tableModel.getValueAt(selectedRow, 4);
        String currentAddress = (String) tableModel.getValueAt(selectedRow, 5);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 6);
        String currentAccount = (String) tableModel.getValueAt(selectedRow, 7);
        String currentDesignation = (String) tableModel.getValueAt(selectedRow, 8);
        String currentSalary = (String) tableModel.getValueAt(selectedRow, 9);
        int currentLeaves = (int) tableModel.getValueAt(selectedRow, 10);
        String currentBonus = (String) tableModel.getValueAt(selectedRow, 11);
        
        JTextField nameField = new JTextField(currentName);
        JTextField ageField = new JTextField(String.valueOf(currentAge));
        JTextField nicField = new JTextField(currentNIC);
        JTextField contactField = new JTextField(currentContact);
        JTextField addressField = new JTextField(currentAddress);
        JTextField emailField = new JTextField(currentEmail);
        JTextField accountField = new JTextField(currentAccount);
        JComboBox<String> designationCombo = new JComboBox<>(new String[]{"Driver", "Officer", "Helper"});
        designationCombo.setSelectedItem(currentDesignation);
        JTextField salaryField = new JTextField(currentSalary);
        JTextField leavesField = new JTextField(String.valueOf(currentLeaves));
        JTextField bonusField = new JTextField(currentBonus);
        
        JPanel panel = new JPanel(new GridLayout(12, 2, 5, 5));
        panel.add(new JLabel("Employee ID:")); panel.add(new JLabel(String.valueOf(empId)));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Age:")); panel.add(ageField);
        panel.add(new JLabel("NIC:")); panel.add(nicField);
        panel.add(new JLabel("Contact:")); panel.add(contactField);
        panel.add(new JLabel("Address:")); panel.add(addressField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Account No:")); panel.add(accountField);
        panel.add(new JLabel("Designation:")); panel.add(designationCombo);
        panel.add(new JLabel("Salary:")); panel.add(salaryField);
        panel.add(new JLabel("Leaves Taken:")); panel.add(leavesField);
        panel.add(new JLabel("Bonus:")); panel.add(bonusField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Employee (ID: " + empId + ")",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText().trim();
                int newAge = Integer.parseInt(ageField.getText().trim());
                String newNIC = nicField.getText().trim();
                String newContact = contactField.getText().trim();
                String newAddress = addressField.getText().trim();
                String newEmail = emailField.getText().trim();
                String newAccount = accountField.getText().trim();
                String newDesignation = (String) designationCombo.getSelectedItem();
                double newSalary = Double.parseDouble(salaryField.getText().trim());
                int newLeaves = Integer.parseInt(leavesField.getText().trim());
                double newBonus = Double.parseDouble(bonusField.getText().trim());
                
                if (newName.isEmpty() || newNIC.isEmpty() || newContact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Required fields cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String sql = "UPDATE Employee SET Name=?, Age=?, NIC=?, Contact=?, Address=?, Email=?, " +
                             "AccountNumber=?, Designation=?, Salary=?, LeavesTaken=?, Bonus=? WHERE EmployeeID=?";
                
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, newName);
                    ps.setInt(2, newAge);
                    ps.setString(3, newNIC);
                    ps.setString(4, newContact);
                    ps.setString(5, newAddress);
                    ps.setString(6, newEmail);
                    ps.setString(7, newAccount);
                    ps.setString(8, newDesignation);
                    ps.setDouble(9, newSalary);
                    ps.setInt(10, newLeaves);
                    ps.setDouble(11, newBonus);
                    ps.setInt(12, empId);
                    
                    int rowsAffected = ps.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadEmployeeData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update employee.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Age, Salary, Leaves, and Bonus.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewEmployeeFrame::new);
    }
}