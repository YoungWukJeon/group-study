package _3;

import lombok.Getter;

import java.util.Random;

@Getter
public class TempInfo {
    public static final Random random = new Random();

    private final String town;
    private final int temp;

    public TempInfo(String town, int temp) {
        this.town = town;
        this.temp = temp;
    }

    // 정적 팩토리 메서드를 이용해 해당 도시의 TempInfo 인스턴스를 만든다.
    public static TempInfo fetch(String town) {
        if (random.nextInt(10) == 0) {  // 1/10 확률로 온도 가져오기 작업이 실패
            throw new RuntimeException("Error!");
        }
        return new TempInfo(town, random.nextInt(100)); // 사이에서 임의의 화씨 온도를 반환
    }

    @Override
    public String toString() {
        return town + " : " + temp;
    }
}
