package tacos.data;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.repository.CrudRepository;
import reactor.core.publisher.Mono;
import tacos.User;

import java.util.UUID;

public interface UserRepository extends ReactiveCassandraRepository<User, UUID> {
    @AllowFiltering
    Mono<User> findByUsername(String username);

    @AllowFiltering
    Mono<User> findByEmail(String email);
}