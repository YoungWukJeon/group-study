package tacos.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import tacos.Taco;

import java.util.UUID;

public interface TacoRepository extends ReactiveCrudRepository<Taco, UUID> {
}