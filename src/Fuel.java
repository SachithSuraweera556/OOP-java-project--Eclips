public class Fuel {
    private int fuelID;
    private String location;
    private double costPerLiter;

    public Fuel(int fuelID, String location, double costPerLiter) {
        this.fuelID = fuelID;
        this.location = location;
        this.costPerLiter = costPerLiter;
    }

    // Getter methods
    public int getFuelID() {
        return fuelID;
    }

    public String getLocation() {
        return location;
    }

    public double getCostPerLiter() {
        return costPerLiter;
    }

    // Setter methods
    public void setFuelID(int fuelID) {
        this.fuelID = fuelID;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCostPerLiter(double costPerLiter) {
        this.costPerLiter = costPerLiter;
    }

    // Business method
    public void locationFuelCost() {
        System.out.println("Fuel cost at " + location + ": Rs. " + costPerLiter + " per liter");
    }

    public double calculateTotalCost(double liters) {
        return liters * costPerLiter;
    }

    @Override
    public String toString() {
        return "Fuel{" +
                "fuelID=" + fuelID +
                ", location='" + location + '\'' +
                ", costPerLiter=" + costPerLiter +
                '}';
    }
}