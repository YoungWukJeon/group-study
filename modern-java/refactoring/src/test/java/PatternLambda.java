import fileReader.ClassFileProcessor;
import org.junit.Assert;
import org.junit.Test;
import validator.IsNumericValidator;
import validator.Validator;
import validator.ValidatorStrategy;

import java.io.IOException;
import java.util.AbstractList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatternLambda {
    @Test
    public void 조건부_연기_실행(){
        Logger logger = Logger.getLogger("Test");
        String msg = "abcdefg";

        // 단순히 로깅
        logger.log(Level.FINER, "Test logging: " + msg);

        // 람다를 이용한 로깅
        logger.log(Level.FINER, () -> "Test logging: " + msg);
    }

    @Test
    public void 실행_어라운드() throws IOException {
        ClassFileProcessor processor = new ClassFileProcessor();
        processor.run();
    }

    @Test
    public void 전략패턴(){
        Validator numeric = new Validator(new IsNumericValidator());
        Assert.assertFalse(numeric.validate("aaa"));
        Assert.assertTrue(numeric.validate("123"));

        // 람다
        Validator lambdaNum = new Validator((String str) -> str.matches("\\d+"));
        Assert.assertFalse(lambdaNum.validate("aaa"));
        Assert.assertTrue(lambdaNum.validate("123"));
    }
}
