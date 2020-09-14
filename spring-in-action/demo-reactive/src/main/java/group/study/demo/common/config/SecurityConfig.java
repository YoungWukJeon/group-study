package group.study.demo.common.config;

import group.study.demo.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;


//@EnableWebFluxSecurity
public class SecurityConfig {
//    @Autowired
//    private AuthService authService;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
////    @Override
////    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////        auth.userDetailsService(authService)
////                .passwordEncoder(passwordEncoder);
////    }
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http.authorizeExchange()
////                .pathMatchers("/user/**")
////                    .hasRole("USER")
////                .pathMatchers("/h2-console/**")
////                    .permitAll()
//                .anyExchange()
//                    .permitAll()
//                .and()
//                    .httpBasic()
//                .and()
//                    .csrf()
//                        .requireCsrfProtectionMatcher(ServerWebExchangeMatchers.pathMatchers("/h2-console/**"))
//                .and()
//                    .headers()
//                        .frameOptions()
//                            .disable()
////                .and()
////                    .formLogin()
////                        .loginPage("/login")
//    //                        .usernameParameter("email")
//    //                        .passwordParameter("password")
//    //                        .defaultSuccessUrl("/", false)
////                .and()
////                    .logout()
////                    .logoutUrl("/")
//                .and().build();
//    }
}
