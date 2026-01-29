package dev.purple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.authorization.OAuth2ReactiveAuthorizationManagers.hasScope;

@EnableWebFluxSecurity
@RestController
@SpringBootApplication
public class ResourceApplication
{
    @GetMapping("/")
    public String index (@AuthenticationPrincipal Jwt jwt)
    {
        return String.format("Hello, %s!", jwt.getSubject());
    }

    @Value("${br.dev.purpura.study.oauth2.namespace}")
    String authzNamespace;

    @Bean
    public SecurityWebFilterChain securityFilterChain (ServerHttpSecurity http)
    {
        // @formatter:off
        http
            .authorizeExchange(authorize -> authorize
                .pathMatchers("/**").access(hasScope(authzNamespace+".read"))
                .anyExchange().authenticated()
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
