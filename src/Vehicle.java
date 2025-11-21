public class Vehicle {
    private int vehicleID;
    private String vehicleNumber;
    private String vehicleType;
    private String model;

    public Vehicle(int vehicleID, String vehicleNumber, String vehicleType, String model) {
        this.vehicleID = vehicleID;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.model = model;
    }

    // Getter methods
    public int getVehicleID() {
        return vehicleID;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getModel() {
        return model;
    }

    // Setter methods
    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // Business methods
    public void addVehicle() {
        System.out.println("Vehicle added: " + vehicleNumber);
    }

    public void removeVehicle() {
        System.out.println("Vehicle removed: " + vehicleNumber);
    }

    public void updateVehicle() {
        System.out.println("Vehicle updated: " + vehicleNumber);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID=" + vehicleID +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}