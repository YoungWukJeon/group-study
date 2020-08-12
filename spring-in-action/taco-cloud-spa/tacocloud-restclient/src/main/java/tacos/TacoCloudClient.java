package tacos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TacoCloudClient {
    private RestTemplate rest;
    private Traverson traverson;

    public TacoCloudClient(RestTemplate rest, Traverson traverson) {
        this.rest = rest;
        this.traverson = traverson;
    }

    public Ingredient getIngredientById(String ingredientId) {
        return rest.getForObject("http://localhost:8080/ingredients/{id}", Ingredient.class, ingredientId);
    }

//    public Ingredient getIngredientById(String ingredientId) {
//       Map<String, String> urlVariables = new HashMap<> ();
//       urlVariables.put("id", ingredientId);
//       return rest.getForObject("http://localhost:8080/ingredients/{id}", Ingredient.class, urlVariables);
//     }

//    public Ingredient getIngredientById(String ingredientId) {
//        Map<String, String> urlVariables = new HashMap<>();
//        urlVariables.put("id", ingredientId);
//        URI url = UriComponentsBuilder
//                .fromHttpUrl("http://localhost:8080/ingredients/{id}")
//                .build(urlVariables);
//        return rest.getForObject(url, Ingredient.class);
//    }

//    public Ingredient getIngredientById(String ingredientId) {
//        ResponseEntity<Ingredient> responseEntity =
//                rest.getForEntity("http://localhost:8080/ingredients/{id}",
//                        Ingredient.class, ingredientId);
//        log.info("Fetched time: " +
//                responseEntity.getHeaders().getDate());
//        return responseEntity.getBody();
//    }

    public List<Ingredient> getAllIngredients() {
        return rest.exchange("http://localhost:8080/ingredients",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Ingredient>>() {})
                .getBody();
    }

    public void updateIngredient(Ingredient ingredient) {
        rest.put("http://localhost:8080/ingredients/{id}",
                ingredient, ingredient.getId());
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        return rest.postForObject("http://localhost:8080/ingredients",
                ingredient, Ingredient.class);
    }

//     public URI createIngredient(Ingredient ingredient) {
//        return rest.postForLocation("http://localhost:8080/ingredients",
//                ingredient, Ingredient.class);
//     }

//    public Ingredient createIngredient(Ingredient ingredient) {
//        ResponseEntity<Ingredient> responseEntity =
//                rest.postForEntity("http://localhost:8080/ingredients",
//                        ingredient,
//                        Ingredient.class);
//        log.info("New resource created at " +
//                responseEntity.getHeaders().getLocation());
//        return responseEntity.getBody();
//    }

    public void deleteIngredient(Ingredient ingredient) {
        rest.delete("http://localhost:8080/ingredients/{id}",
                ingredient.getId());
    }

    public Iterable<Ingredient> getAllIngredientsWithTraverson() {
        ParameterizedTypeReference<Resources<Ingredient>> ingredientType =
                new ParameterizedTypeReference<Resources<Ingredient>>() {};

        Resources<Ingredient> ingredientRes =
                traverson
                        .follow("ingredients")
                        .toObject(ingredientType);

        Collection<Ingredient> ingredients = ingredientRes.getContent();

        return ingredients;
    }

    public Ingredient addIngredient(Ingredient ingredient) {
        String ingredientsUrl = traverson
                .follow("ingredients")
                .asLink()
                .getHref();

        return rest.postForObject(ingredientsUrl,
                ingredient,
                Ingredient.class);
    }

    public Iterable<Taco> getRecentTacosWithTraverson() {
        ParameterizedTypeReference<Resources<Taco>> tacoType =
                new ParameterizedTypeReference<Resources<Taco>>() {};

        Resources<Taco> tacoRes =
                traverson
                        .follow("tacos")
                        .follow("recents")
                        .toObject(tacoType);

        // Alternatively, list the two paths in the same call to follow()
//    Resources<Taco> tacoRes =
//        traverson
//          .follow("tacos", "recents")
//          .toObject(tacoType);

        return tacoRes.getContent();
    }
}
