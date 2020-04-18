package model;

import lombok.Data;

@Data
public class Score {
    private int math;
    private int english;
    private int total;
    private double average;

    public Score(int math, int english) {
        this.math = math;
        this.english = english;
    }
}
