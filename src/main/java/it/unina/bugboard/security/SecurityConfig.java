package it.unina.bugboard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
	
	public static final String ADMINROLE = "ADMIN";
	public static final String USERROLE = "USER";
	
    @Bean
    public SecurityFilterChain chain(HttpSecurity http, JwtFilter jwtFilter){

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/bugboard/login").permitAll()
                .requestMatchers("/bugboard/admin/**").hasRole(ADMINROLE)
                .requestMatchers("/bugboard/user/**").hasAnyRole(USERROLE, ADMINROLE)
                .requestMatchers("/bugboard/issue/**").hasAnyRole(USERROLE,ADMINROLE)
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}