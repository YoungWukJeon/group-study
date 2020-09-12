package tacos.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import tacos.PaymentMethod;

import java.util.UUID;

public interface PaymentMethodRepository extends ReactiveCrudRepository<PaymentMethod, UUID> {
    Mono<PaymentMethod> findByUserUsername(String username);
}