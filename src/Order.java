public class Order {
    private int orderID;
    private String orderStatus;

    public Order(int orderID, String orderStatus) {
        this.orderID = orderID;
        this.orderStatus = orderStatus;
    }

    // Getter methods
    public int getOrderID() {
        return orderID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    // Setter methods
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    // Business method
    public void maintainOrderState() {
        System.out.println("Order #" + orderID + " state maintained. Current status: " + orderStatus);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }
}