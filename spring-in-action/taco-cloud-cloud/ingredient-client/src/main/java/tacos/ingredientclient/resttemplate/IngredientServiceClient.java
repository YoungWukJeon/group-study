package tacos.ingredientclient.resttemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tacos.ingredientclient.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Conditional(NotFeignAndNotWebClientCondition.class)
public class IngredientServiceClient {
    private RestTemplate rest;

    public IngredientServiceClient(@LoadBalanced RestTemplate rest) {
        this.rest = rest;
    }

    @HystrixCommand(fallbackMethod="getDefaultIngredientDetails")
    public Ingredient getIngredientById(String ingredientId) {
        return rest.getForObject(
                "http://ingredient-service/ingredients/{id}",
                Ingredient.class, ingredientId);
    }

    private Ingredient getDefaultIngredientDetails(String ingredientId) {
        if (ingredientId.equals("FLTO")) {
            return new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP);
        } else if (ingredientId.equals("GRBF")) {
            return new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN);
        } else {
            return new Ingredient("CHED", "Shredded Cheddar", Ingredient.Type.CHEESE);
        }
    }

    @HystrixCommand(fallbackMethod="getDefaultIngredients",
            commandProperties={
                    @HystrixProperty(
                            name="execution.isolation.thread.timeoutInMilliseconds",
                            value="500"),
                    @HystrixProperty(
                            name="circuitBreaker.requestVolumeThreshold",
                            value="30"),
                    @HystrixProperty(
                            name="circuitBreaker.errorThresholdPercentage",
                            value="25"),
                    @HystrixProperty(
                            name="metrics.rollingStats.timeInMilliseconds",
                            value="20000"),
                    @HystrixProperty(
                            name="circuitBreaker.sleepWindowInMilliseconds",
                            value="60000")
            })
    public Iterable<Ingredient> getAllIngredients() {
        Ingredient[] ingredients = rest.getForObject(
                "http://ingredient-service/ingredients", Ingredient[].class);
        return Arrays.asList(ingredients);
    }

    private Iterable<Ingredient> getDefaultIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(
                "FLTO", "Flour Tortilla", Ingredient.Type.WRAP));
        ingredients.add(new Ingredient(
                "GRBF", "Ground Beef", Ingredient.Type.PROTEIN));
        ingredients.add(new Ingredient(
                "CHED", "Shredded Cheddar", Ingredient.Type.CHEESE));
        return ingredients;
    }
}
