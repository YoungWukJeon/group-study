package group.study.demo.common.config;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

//@Slf4j
@Configuration
public class CustomConnectionFactoryInitializer {
    @Bean
    public ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
//    @Bean
//    public ApplicationRunner seeder(DatabaseClient client) {
//        return args -> getSchema().flatMap(sql -> executeSql(client, sql))
////                .then(getData().flatMap(sql -> executeSql(client, sql)))
//                .subscribe(count -> log.info("Schema created"));
//    }
//
//    private Mono<String> getSchema() throws URISyntaxException {
//        Path path1 = Paths.get(ClassLoader.getSystemResource("schema.sql").toURI());
//        Path path2 = Paths.get(ClassLoader.getSystemResource("data.sql").toURI());
//
//        return Flux.using(() -> Stream.of(Files.lines(path1), Files.lines(path2)).flatMap(f -> f), Flux::fromStream, BaseStream::close)
//                .reduce((l1, l2) -> l1 + "\n" + l2);
////        return Flux.using(() -> Files.lines(path1), Flux::fromStream, BaseStream::close
////                .reduce((l1, l2) -> l1 + "\n" + l2).then(getData());
//    }
//
//    private Mono<String> getData() throws URISyntaxException {
//        Path path = Paths.get(ClassLoader.getSystemResource("data.sql").toURI());
//        return Flux.using(() -> Files.lines(path), Flux::fromStream, BaseStream::close)
//                .reduce((l1, l2) -> l1 + "\n" + l2);
//    }
//
//    private Mono<Integer> executeSql(DatabaseClient client, String sql) {
//        return client.execute(sql).fetch().rowsUpdated();
//    }
}