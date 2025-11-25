import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportService {

    // 1. Count total orders for a selected Month and Year
    public static int getMonthlyOrderCount(int year, int month) {
        int count = 0;
        // SQL query using built-in Date functions
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

    // 2. Sum total fuel usage for a selected Month and Year
    public static double getMonthlyFuelUsage(int year, int month) {
        double totalFuel = 0;
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

    // 3. Calculate Estimated Cost (Assuming Rs. 350 per Liter)
    public static double getEstimatedFuelCost(int year, int month) {
        double liters = getMonthlyFuelUsage(year, month);
        double costPerLiter = 350.0; // You can update this rate if fuel prices change
        return liters * costPerLiter;
    }
}