package com.cine.ms_reservas.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Valida que el claim "aud" del JWT coincida con la audiencia esperada.
 * Solo se registra desde {@link SecurityConfig}, activo para cualquier perfil
 * distinto de "dev" (en dev no se valida token).
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String audience;

    public AudienceValidator(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        // Si no se configuro audiencia, no se valida.
        if (audience == null || audience.isBlank()) {
            return OAuth2TokenValidatorResult.success();
        }
        if (token.getAudience() != null) {
            // v1 emite aud: api://{client-id}, v2 emite aud: {client-id} (GUID puro).
            // Aceptar ambos para ser tolerante al requestedAccessTokenVersion.
            String stripped = audience.replace("api://", "");
            String apiForm = audience.startsWith("api://") ? audience : "api://" + audience;
            if (token.getAudience().contains(audience)
                    || token.getAudience().contains(stripped)
                    || token.getAudience().contains(apiForm)) {
                return OAuth2TokenValidatorResult.success();
            }
        }
        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "La audiencia es invalida, se esperaba: " + audience + " (se acepta GUID puro o con prefijo api://)",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
