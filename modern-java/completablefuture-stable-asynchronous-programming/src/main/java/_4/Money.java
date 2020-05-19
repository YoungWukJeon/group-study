package _4;

import lombok.Getter;

@Getter
public enum Money {
    // 20200520 05:35 daum 환율 기준
    USD(1), KRW(1226.00) , JPY(107.59), EUR(0.91);

    private final double ratePerDollar;

    Money(double ratePerDollar) {
        this.ratePerDollar = ratePerDollar;
    }
}
