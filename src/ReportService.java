import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportService {

    // --- EXISTING MONTHLY METHODS ---

    public static int getMonthlyOrderCount(int year, int month) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS Total FROM Orders WHERE YEAR(LoadedDate) = ? AND MONTH(LoadedDate) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt("Total");
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    public static double getMonthlyFuelUsage(int year, int month) {
        double totalFuel = 0;
        String sql = "SELECT SUM(fuel_needed) AS TotalFuel FROM fuel_log WHERE YEAR(date) = ? AND MONTH(date) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) totalFuel = rs.getDouble("TotalFuel");
        } catch (Exception e) { e.printStackTrace(); }
        return totalFuel;
    }

    public static double getEstimatedFuelCost(int year, int month) {
        return getMonthlyFuelUsage(year, month) * 350.0;
    }

    // --- NEW DASHBOARD WIDGET METHODS ---

    public static int getTotalCount(String tableName) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS Total FROM " + tableName;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) count = rs.getInt("Total");
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }
    
    // Specific method for Orders to maybe filter by "Pending" later if you want
    public static int getTotalOrders() {
        return getTotalCount("Orders");
    }
}