import model.Car;
import model.Insurance;
import model.Person;

public class OriginalNullProcessor {
    public String getCarInsuranceName(Person person) {
        return person.getCar().getInsurance().getName();
    }

    public String getCarInsuranceNameUsingNullProcessing(Person person) {
        if (person.getCar() != null) {
            Car car = person.getCar();
            if (car.getInsurance() != null) {
                Insurance insurance = car.getInsurance();
                return insurance.getName();
            }
        }
        return "Unknown";
    }

    public String getCarInsuranceNameUsingNullProcessingWithManyExit(Person person) {
        if (person == null) {
            return "Unknown";
        }
        Car car = person.getCar();
        if (car == null) {
            return "Unknown";
        }
        Insurance insurance = car.getInsurance();
        if (insurance == null) {
            return "Unknown";
        }
        return insurance.getName();
    }

    public static void main(String[] args) {
        OriginalNullProcessor main = new OriginalNullProcessor();
        Person person = new Person();
        person.setCar(new Car());

        System.out.println(main.getCarInsuranceNameUsingNullProcessing(person));
    }
}