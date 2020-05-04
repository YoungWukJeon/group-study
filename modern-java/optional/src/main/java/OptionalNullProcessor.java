import model.optional.Car;
import model.optional.Insurance;
import model.optional.Person;

import java.util.Optional;

public class OptionalNullProcessor {
    public String getCarInsuranceNameUsingOptional(Optional<Person> person) {
        return person.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }

    public static void main(String[] args) {
        OptionalNullProcessor main = new OptionalNullProcessor();
        System.out.println(main.getCarInsuranceNameUsingOptional(
                Optional.of(Person.builder()
                        .car(Optional.of(Car.builder()
                                .insurance(Optional.of(Insurance.builder()
                                        .name(null).build())).build())).build())));
    }
}
