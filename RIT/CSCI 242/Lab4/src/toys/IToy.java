package toys;

public interface IToy {
    void play(int minutes);
    void increaseWear(double amount);
    int getProductCode();
    String getName();
    int getHappiness();
    double getWear();
    boolean isRetired();
    String toString();
}
