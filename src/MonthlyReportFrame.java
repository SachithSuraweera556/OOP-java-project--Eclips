import javax.swing.*;
import java.awt.*;
import java.time.Year;

public class MonthlyReportFrame extends JFrame {
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JButton generateButton;
    private JTextArea reportArea;

    public MonthlyReportFrame() {
        setTitle("Monthly Summary Report");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Selectors ---
        JPanel topPanel = new JPanel(new FlowLayout());
        
        String[] months = {"January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December"};
        monthCombo = new JComboBox<>(months);
        
        // Auto-fill years (Current Year +/- 5 years)
        yearCombo = new JComboBox<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearCombo.addItem(i);
        }
        yearCombo.setSelectedItem(currentYear);

        generateButton = new JButton("Generate Report");
        generateButton.setBackground(new Color(0, 123, 255)); // Blue button
        generateButton.setForeground(Color.WHITE);

        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthCombo);
        topPanel.add(new JLabel("Year:"));
        topPanel.add(yearCombo);
        topPanel.add(generateButton);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Report Text ---
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        reportArea.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);

        // --- Action Listener ---
        generateButton.addActionListener(e -> generateReport());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateReport() {
        // 1. Get User Input
        int monthIndex = monthCombo.getSelectedIndex() + 1; // Jan=1, Feb=2
        int year = (int) yearCombo.getSelectedItem();
        String monthName = (String) monthCombo.getSelectedItem();

        // 2. Get Data from Database (using our Service)
        int totalOrders = ReportService.getMonthlyOrderCount(year, monthIndex);
        double totalFuel = ReportService.getMonthlyFuelUsage(year, monthIndex);
        double estimatedCost = ReportService.getEstimatedFuelCost(year, monthIndex);

        // 3. Design the Report Output
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("       MONTHLY TRANSPORT REPORT          \n");
        sb.append("=========================================\n\n");
        
        sb.append(" Period: " + monthName + " " + year + "\n");
        sb.append(" Generated on: " + java.time.LocalDate.now() + "\n");
        sb.append("-----------------------------------------\n\n");
        
        sb.append(" [ PERFORMANCE ]\n");
        sb.append(String.format(" 📦 Total Orders       :   %d\n", totalOrders));
        
        sb.append("\n [ EXPENSES ]\n");
        sb.append(String.format(" ⛽ Fuel Consumed      :   %.2f Liters\n", totalFuel));
        sb.append(String.format(" 💰 Est. Fuel Cost     :   Rs. %.2f\n", estimatedCost));
        
        sb.append("\n-----------------------------------------\n");
        sb.append(" * Cost based on Rs. 350/Liter rate.\n");
        sb.append("=========================================\n");

        // 4. Show it
        reportArea.setText(sb.toString());
    }
}