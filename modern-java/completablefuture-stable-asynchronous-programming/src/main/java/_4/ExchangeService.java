package _4;

public class ExchangeService {
    public static final double DEFAULT_RATE = 1.00d;

    public double getRate(Money m1, Money m2) {
        return Math.round(m2.getRatePerDollar() / m1.getRatePerDollar() * 100) / 100.00;
    }
}
