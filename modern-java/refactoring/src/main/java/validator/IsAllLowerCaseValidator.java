package validator;

public class IsAllLowerCaseValidator implements ValidatorStrategy {
    @Override
    public boolean execute(String str) {
        return str.matches("[a-z]+");
    }
}
