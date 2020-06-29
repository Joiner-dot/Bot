public class Bank {

    private String name;
    private double sell;
    private double buy;

    Bank(String name, double sell, double buy) {
        this.name = name;
        this.sell = sell;
        this.buy = buy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }


}
