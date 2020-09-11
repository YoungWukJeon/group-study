package group.study.demo.common.config;

//import io.r2dbc.h2.H2ConnectionConfiguration;
//import io.r2dbc.h2.H2ConnectionFactory;
//import io.r2dbc.h2.H2ConnectionOption;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
//public class R2dbcH2Config extends AbstractR2dbcConfiguration {
public class R2dbcH2Config {
//    @Override
//    public ConnectionFactory connectionFactory() {
//        H2ConnectionConfiguration h2ConnectionConfiguration =
//                H2ConnectionConfiguration.builder()
//                        .inMemory("demo")
//                        .username("sa")
//                        .property(H2ConnectionOption.DB_CLOSE_DELAY, "-1")
//                        .property(H2ConnectionOption.DB_CLOSE_ON_EXIT, "FALSE")
//                        .build();
//        return new H2ConnectionFactory(h2ConnectionConfiguration);
//    }

    @Bean
    public ReactiveTransactionManager transactionManager(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}