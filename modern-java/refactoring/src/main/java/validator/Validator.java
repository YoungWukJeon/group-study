package validator;

public class Validator {
    private ValidatorStrategy validatorStrategy;

    public Validator(ValidatorStrategy validatorStrategy){
        this.validatorStrategy = validatorStrategy;
    }

    public boolean validate(String str){
        return validatorStrategy.execute(str);
    }
}
