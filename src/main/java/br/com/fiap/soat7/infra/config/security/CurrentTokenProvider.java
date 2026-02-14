package br.com.fiap.soat7.infra.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentTokenProvider {

    public String getToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }

        return null;
    }
}