package dev.purple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@EnableWebSecurity
@RestController
@SpringBootApplication
public class ResourceApplication
{
    @GetMapping("/")
    public String index (@AuthenticationPrincipal Jwt jwt)
    {
        return String.format("Hello, %s!", jwt.getSubject());
    }

    @Value("${app.oauth2.scope-prefix:dev.purple}")
    String scopePrefix;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http)
    {
        // @formatter:off
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**").access(hasScope(this.scopePrefix+".read"))
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        // @formatter:on
        return http.build();
    }

    static void main (String[] args)
    {
        SpringApplication.run(ResourceApplication.class, args);
    }
}
