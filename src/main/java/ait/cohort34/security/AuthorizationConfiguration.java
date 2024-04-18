package ait.cohort34.security;

import ait.cohort34.accounting.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@RequiredArgsConstructor
public class AuthorizationConfiguration {
    final CustomWebSecurity webSecurity;

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

        http.authorizeHttpRequests(authorize -> authorize
                // от частного к общему!
                .requestMatchers("/account/register", "/forum/posts/**").permitAll()
                .requestMatchers("/account/user/{login}/role/{role}").hasRole(Role.ADMINISTRATOR.name())
                .requestMatchers(HttpMethod.PUT, "/account/user/{login}").access(new WebExpressionAuthorizationManager("#login == authentication.name"))
                .requestMatchers(HttpMethod.DELETE, "/account/user/{login}").access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMINISTRATOR')"))
                .requestMatchers(HttpMethod.POST, "/forum/post/{author}").access(new WebExpressionAuthorizationManager("#author == authentication.name"))
                .requestMatchers(HttpMethod.PUT, "/forum/post/{id}/comment/{author}").access(new WebExpressionAuthorizationManager("#author == authentication.name"))
//                .requestMatchers(HttpMethod.PUT, "/forum/post/{id}").access((authentication, context) -> new AuthorizationDecision(webSecurity.checkPostAuthor(context.getVariables().get("id"), authentication.get().getName()) || context.getRequest().isUserInRole(Role.MODERATOR.name())))
                .requestMatchers(HttpMethod.DELETE, "/forum/post/{id}").access((authentication, context) -> {
                    boolean checkAuthor = webSecurity.checkPostAuthor(context.getVariables().get("id"), authentication.get().getName());
                    boolean checkModerator = context.getRequest().isUserInRole(Role.MODERATOR.name());
                    return new AuthorizationDecision(checkAuthor || checkModerator);
                        })
                .anyRequest().authenticated() // самый общий риквест, просто аутентификация!
        );
        return http.build();
    }
}
