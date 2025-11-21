public class Employee {
    protected int employeeID;
    protected String name;
    protected int age;
    protected String NIC;
    protected String contact;
    protected String address;
    protected String email;
    protected String accountNumber;
    protected double salary;
    protected String designation;
    protected int leavesTaken;
    protected double bonus;

    // Constructor with all fields (for AddEmployeeFrame)
    public Employee(int employeeID, String name, int age, String NIC, String contact, 
                    String address, String email, String accountNumber, double salary, 
                    String designation, int leavesTaken, double bonus) {
        this.employeeID = employeeID;
        this.name = name;
        this.age = age;
        this.NIC = NIC;
        this.contact = contact;
        this.address = address;
        this.email = email;
        this.accountNumber = accountNumber;
        this.salary = salary;
        this.designation = designation;
        this.leavesTaken = leavesTaken;
        this.bonus = bonus;
    }

    // Legacy constructor (for backward compatibility with Driver, Officer, Helper)
    public Employee(int employeeID, String name, int age, String NIC, String contact, 
                    String address, double salary, String designation) {
        this(employeeID, name, age, NIC, contact, address, "", "", salary, designation, 0, 0.0);
    }

    // ============ GETTER METHODS ============
    public int getEmployeeID() {
        return employeeID;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getNIC() {
        return NIC;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getSalary() {
        return salary;
    }

    public String getDesignation() {
        return designation;
    }

    public int getLeavesTaken() {
        return leavesTaken;
    }

    public double getBonus() {
        return bonus;
    }

    // ============ SETTER METHODS ============
    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setLeavesTaken(int leavesTaken) {
        this.leavesTaken = leavesTaken;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    // ============ BUSINESS METHODS ============
    public double calculateSalary() {
        // Basic salary + bonus - deductions for leaves
        double leaveDeduction = leavesTaken * 1000; // Rs. 1000 per leave
        double totalSalary = salary + bonus - leaveDeduction;
        return Math.max(totalSalary, 0); // Ensure non-negative
    }

    public void role() {
        System.out.println("Role: " + designation);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + employeeID +
                ", name='" + name + '\'' +
                ", designation='" + designation + '\'' +
                ", salary=" + salary +
                '}';
    }
}