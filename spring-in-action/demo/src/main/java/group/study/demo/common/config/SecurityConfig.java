package group.study.demo.common.config;

import group.study.demo.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private void inMemory(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user1")
                .password("{noop}pass1")
                .authorities("ROLE_USER");
    }

    @Autowired
    private DataSource dataSource;
    private void jdbc(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource);
    }
    // jdbc 테이블 생성 쿼리 추가


    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        inMemory(auth);
//        jdbc(auth);
        auth.userDetailsService(customUserDetailsService)
            .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/**", "/order")
                .access("hasRole('ROLE_USER')")
                .antMatchers("/", "/**")
                .access("permitAll()")
                .and()
                .httpBasic();
    }
}
