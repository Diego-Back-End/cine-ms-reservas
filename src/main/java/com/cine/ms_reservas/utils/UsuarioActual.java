package com.cine.ms_reservas.utils;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Unico lugar donde se obtiene el usuario autenticado.
 * Ningun controller ni service debe leer el token directamente: todos deben
 * depender de esta clase.
 *
 * <p>Lee los claims "sub" (o "oid") y "email" del JWT emitido por Cognito.</p>
 */
@Component
public class UsuarioActual {

    public String getUsuarioId() {
        Jwt jwt = jwtActual();
        String sub = jwt.getSubject();
        if (sub != null && !sub.isBlank()) {
            return sub;
        }
        return jwt.getClaimAsString("oid");
    }

    public String getEmail() {
        String email = jwtActual().getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new InsufficientAuthenticationException(
                    "El token no contiene el claim obligatorio 'email'");
        }
        return email;
    }

    private Jwt jwtActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad");
    }
}
