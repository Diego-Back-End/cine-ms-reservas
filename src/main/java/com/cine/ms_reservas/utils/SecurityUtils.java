package com.cine.ms_reservas.utils;

import org.springframework.security.oauth2.jwt.Jwt;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    // Obtiene el identificador del usuario autenticado desde el token.
    // Se prioriza "sub" y, si no viene, se usa el claim "oid" de Azure Entra ID.
    public static String getUsuarioId(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        String subject = jwt.getSubject();
        if (subject != null && !subject.isBlank()) {
            return subject;
        }
        Object oid = jwt.getClaim("oid");
        return oid != null ? oid.toString() : null;
    }
}
