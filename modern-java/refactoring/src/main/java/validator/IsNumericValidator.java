package validator;

public class IsNumericValidator implements ValidatorStrategy{
    @Override
    public boolean execute(String str) {
        return str.matches("\\d+");
    }
}
