package group.study.demo.common.config;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.h2.H2ConnectionOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.BaseStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class DatabaseSchemaInitializer {
    private static DatabaseClient client;

    public static void init() {
        H2ConnectionConfiguration h2ConnectionConfiguration =
                H2ConnectionConfiguration.builder()
                        .inMemory("demo")
                        .username("sa")
                        .property(H2ConnectionOption.DB_CLOSE_DELAY, "-1")
                        .property(H2ConnectionOption.DB_CLOSE_ON_EXIT, "FALSE")
                        .build();
        client = DatabaseClient.create(new H2ConnectionFactory(h2ConnectionConfiguration));
        createSchema();
    }

    private static void createSchema() {
        getSchema().flatMap(sql -> executeSql(client, sql))
                .subscribe(count -> log.info("Schema created."));
    }

    private static Mono<String> getSchema() {
        try {
            Path schemaPath = Paths.get(ClassLoader.getSystemResource("schema.sql").toURI());
            return Flux.using(() -> Files.lines(schemaPath), Flux::fromStream, BaseStream::close)
                    .reduce((l1, l2) -> l1 + "\n" + l2);
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            return Mono.empty();
        }
    }

    private static Mono<Integer> executeSql(DatabaseClient client, String sql) {
        return client.execute(sql)
                .fetch()
                .rowsUpdated();
    }
}