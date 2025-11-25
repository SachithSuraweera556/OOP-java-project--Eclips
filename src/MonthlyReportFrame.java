import javax.swing.*;
import java.awt.*;
import java.time.Year;

public class MonthlyReportFrame extends JFrame {
    JComboBox<String> monthCombo;
    JComboBox<Integer> yearCombo;
    JButton generateButton;
    JTextArea reportArea;

    public MonthlyReportFrame() {
        setTitle("Monthly Sales & Performance Report");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Date Selection ---
        JPanel topPanel = new JPanel(new FlowLayout());
        
        String[] months = {"January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December"};
        monthCombo = new JComboBox<>(months);
        
        // Auto-populate years (Current Year +/- 5 years)
        yearCombo = new JComboBox<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearCombo.addItem(i);
        }
        yearCombo.setSelectedItem(currentYear);

        generateButton = new JButton("Generate Report");
        generateButton.setBackground(new Color(0, 102, 204));
        generateButton.setForeground(Color.WHITE);

        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthCombo);
        topPanel.add(new JLabel("Year:"));
        topPanel.add(yearCombo);
        topPanel.add(generateButton);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Report Display ---
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setMargin(new Insets(15, 15, 15, 15));
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        // --- Action: Generate Report ---
        generateButton.addActionListener(e -> generateReport());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateReport() {
        // Get selected values
        int monthIndex = monthCombo.getSelectedIndex() + 1; // Jan = 1, Feb = 2...
        int year = (int) yearCombo.getSelectedItem();
        String monthName = (String) monthCombo.getSelectedItem();

        // 1. Fetch Data from Database using ReportService
        int totalOrders = ReportService.getMonthlyOrderCount(year, monthIndex);
        double totalFuel = ReportService.getMonthlyFuelUsage(year, monthIndex);
        double estimatedCost = ReportService.getEstimatedFuelCost(year, monthIndex);

        // 2. Format the Output nicely
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("       MONTHLY TRANSPORT SUMMARY         \n");
        sb.append("=========================================\n\n");
        
        sb.append(String.format(" Period: %s %d \n", monthName, year));
        sb.append("-----------------------------------------\n");
        
        // Sales / Volume Section
        sb.append(" [ PERFORMANCE ]\n");
        sb.append(String.format(" 📦 Total Orders Completed :   %d\n", totalOrders));
        if(totalOrders == 0) {
            sb.append("    (No orders found for this period)\n");
        }
        
        sb.append("\n");
        
        // Expense Section
        sb.append(" [ EXPENSES ]\n");
        sb.append(String.format(" ⛽ Total Fuel Used        :   %.2f Liters\n", totalFuel));
        sb.append(String.format(" 💰 Est. Fuel Cost         :   Rs. %.2f\n", estimatedCost));
        
        sb.append("\n-----------------------------------------\n");
        sb.append(" * Fuel cost calculated at Rs. 350/Liter\n");
        sb.append("=========================================\n");

        reportArea.setText(sb.toString());
    }
}