package tacos.email;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private final String email;
    private List<Taco> tacos = new ArrayList<>();

    public void addTaco(Taco taco) {
        this.tacos.add(taco);
    }
}