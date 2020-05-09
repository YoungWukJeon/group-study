package Game;

public interface Rotatable {
    void setRotationAngle(int angleInDegrees);
    int getRotationAngle();
    default void rotateBy(int angleInDegrees) { // <--| rotateBy 메서드의 기본 구현
        setRotationAngle((getRotationAngle() + angleInDegrees) % 360);
    }
}
