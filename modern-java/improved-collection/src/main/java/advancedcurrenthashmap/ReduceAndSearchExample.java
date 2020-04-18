package advancedcurrenthashmap;

import model.Score;

import java.util.concurrent.ConcurrentHashMap;

public class ReduceAndSearchExample {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Score> scores = new ConcurrentHashMap<> ();
        scores.put("mike", new Score(80, 70));
        scores.put("jane", new Score(60, 30));
        scores.put("john", new Score(90, 100));
        scores.put("cathy", new Score(50, 80));

        useForEach(scores); // 개인 합계, 평균 계산
        useReduce(scores);  // 수학 평균, 영어 평균 계산
        useSearch(scores);  // 영어 점수가 100점인 사람 이름 찾기
    }

    public static void useForEach(ConcurrentHashMap<String, Score> scores) {
        scores.forEach((name, score) -> {
            int math = score.getMath();
            int english = score.getEnglish();
            score.setTotal(math + english);
            score.setAverage(score.getTotal() / 2.0D);
        });

        scores.forEach((name, score) -> System.out.println(name + "->" + score));
    }

    public static void useReduce(ConcurrentHashMap<String, Score> scores) {
        long start = System.currentTimeMillis();
        int mathTotal = scores.reduceValuesToInt(1L, value -> value.getMath(), 0, (left, right) -> left + right);
        int englishTotal = scores.reduceValuesToInt(1L, value -> value.getEnglish(), 0, (left, right) -> left + right);

        double mathAverage = mathTotal / (double) scores.mappingCount();
        double englishAverage = englishTotal / (double) scores.mappingCount();
        System.out.println("MathAverage is " + mathAverage + ", EnglishAverage is " + englishAverage);

        long end = System.currentTimeMillis();
        System.out.println("Calculation takes " + (end - start) + "ms");
    }

    public static void useSearch(ConcurrentHashMap<String, Score> scores) {
        String englishMaster = scores.search(1L, (name, score) -> score.getEnglish() == 100? name: null);
        System.out.println("English Master is " + englishMaster);
    }
}
