package com.cine.ms_reservas.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

/**
 * Valida que el JWT sea un ID token de Amazon Cognito:
 * <ul>
 *   <li>El claim "aud" debe contener el client id configurado.</li>
 *   <li>El claim "token_use" debe ser exactamente "id".</li>
 * </ul>
 * Solo se registra desde {@link SecurityConfig}.
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private static final String TOKEN_USE_CLAIM = "token_use";
    private static final String EXPECTED_TOKEN_USE = "id";

    private final String audience;

    public AudienceValidator(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (audience == null || audience.isBlank()) {
            return failure("La audiencia esperada (COGNITO_CLIENT_ID) no esta configurada");
        }

        List<String> audiences = token.getAudience();
        if (audiences == null || !audiences.contains(audience)) {
            return failure("La audiencia es invalida, se esperaba: " + audience);
        }

        String tokenUse = token.getClaimAsString(TOKEN_USE_CLAIM);
        if (!EXPECTED_TOKEN_USE.equals(tokenUse)) {
            return failure("El token debe ser un ID token (token_use=id)");
        }

        return OAuth2TokenValidatorResult.success();
    }

    private OAuth2TokenValidatorResult failure(String description) {
        OAuth2Error error = new OAuth2Error("invalid_token", description, null);
        return OAuth2TokenValidatorResult.failure(error);
    }
}
