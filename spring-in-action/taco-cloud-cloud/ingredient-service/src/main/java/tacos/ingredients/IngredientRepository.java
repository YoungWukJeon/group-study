package tacos.ingredients;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, String> {
}
