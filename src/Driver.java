public class Driver extends Employee {
    private String licenseNumber;
    private int experience;

    public Driver(int employeeID, String name, int age, String NIC, String contact, 
                  String address, double salary, String designation, 
                  String licenseNumber, int experience) {
        super(employeeID, name, age, NIC, contact, address, salary, designation);
        this.licenseNumber = licenseNumber;
        this.experience = experience;
    }

    // Getter methods
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public int getExperience() {
        return experience;
    }

    // Setter methods
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    // Business method
    public void assignVehicle() {
        System.out.println("Vehicle assigned to driver: " + getName());
    }

    @Override
    public String toString() {
        return "Driver{" +
                "employeeID=" + getEmployeeID() +
                ", name='" + getName() + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", experience=" + experience + " years" +
                '}';
    }
}