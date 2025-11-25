import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportService {

    // 1. Get Total Orders for a specific Month/Year
    public static int getMonthlyOrderCount(int year, int month) {
        int count = 0;
        // Since you changed LoadedDate to DATE, we use YEAR() and MONTH()
        String sql = "SELECT COUNT(*) AS Total FROM Orders WHERE YEAR(LoadedDate) = ? AND MONTH(LoadedDate) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("Total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // 2. Get Total Fuel Consumed for a specific Month/Year
    public static double getMonthlyFuelUsage(int year, int month) {
        double totalFuel = 0;
        // fuel_log also uses DATE, so the logic is the same
        String sql = "SELECT SUM(fuel_needed) AS TotalFuel FROM fuel_log WHERE YEAR(date) = ? AND MONTH(date) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                totalFuel = rs.getDouble("TotalFuel");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalFuel;
    }

    // 3. Estimate Costs (Assuming 1 Liter = Rs. 350)
    // You can change the 350.0 to whatever the current market price is
    public static double getEstimatedFuelCost(int year, int month) {
        double liters = getMonthlyFuelUsage(year, month);
        double costPerLiter = 350.0; 
        return liters * costPerLiter;
    }
}