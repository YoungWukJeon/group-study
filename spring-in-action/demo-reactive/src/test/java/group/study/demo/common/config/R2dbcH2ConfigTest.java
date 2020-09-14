package group.study.demo.common.config;

import group.study.demo.common.converter.ProductEntityReadConverter;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.h2.H2ConnectionOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.dialect.H2Dialect;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.BaseStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class R2dbcH2ConfigTest {
    @Deprecated
    public void init() {
        var h2ConnectionConfiguration =
                H2ConnectionConfiguration.builder()
                        .inMemory("demo")
                        .username("sa")
                        .property(H2ConnectionOption.DB_CLOSE_DELAY, "-1")
                        .property(H2ConnectionOption.DB_CLOSE_ON_EXIT, "FALSE")
                        .build();

        var strategy = new DefaultReactiveDataAccessStrategy(
                H2Dialect.INSTANCE,
                List.of(new ProductEntityReadConverter()));

        var client = DatabaseClient.builder()
                .connectionFactory(new H2ConnectionFactory(h2ConnectionConfiguration))
                .namedParameters(true)
                .dataAccessStrategy(strategy)
                .build();
//        createSchema(client);
    }

    public void createSchema(DatabaseClient client) {
        getSchema().flatMap(sql -> executeSql(client, sql))
                .subscribe(count -> log.info("Schema created."));
    }

    private Mono<String> getSchema() {
        try {
            Path schemaPath = Paths.get(ClassLoader.getSystemResource("schema.sql").toURI());
            return Flux.using(() -> Files.lines(schemaPath), Flux::fromStream, BaseStream::close)
                    .reduce((l1, l2) -> l1 + "\n" + l2);
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            return Mono.empty();
        }
    }

    private Mono<Integer> executeSql(DatabaseClient client, String sql) {
        return client.execute(sql)
                .fetch()
                .rowsUpdated();
    }
}