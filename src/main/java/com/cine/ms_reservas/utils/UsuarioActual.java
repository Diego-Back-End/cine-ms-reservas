package com.cine.ms_reservas.utils;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Unico lugar donde se obtiene el usuario autenticado.
 * Ningun controller ni service debe leer el token directamente: todos deben
 * depender de esta clase.
 *
 * <ul>
 *   <li>Perfil normal: lee los claims "sub" y "email" del JWT.</li>
 *   <li>Perfil dev: devuelve datos fijos para poder probar sin token.</li>
 * </ul>
 */
@Component
public class UsuarioActual {

    private static final String DEV_USUARIO_ID = "usuario-dev";
    private static final String DEV_EMAIL = "dev@cinecloud.cl";

    private final boolean devProfile;

    public UsuarioActual(Environment environment) {
        this.devProfile = environment.acceptsProfiles(Profiles.of("dev"));
    }

    public String getUsuarioId() {
        if (devProfile) {
            return DEV_USUARIO_ID;
        }
        Jwt jwt = jwtActual();
        String sub = jwt.getSubject();
        if (sub != null && !sub.isBlank()) {
            return sub;
        }
        return jwt.getClaimAsString("oid");
    }

    public String getEmail() {
        if (devProfile) {
            return DEV_EMAIL;
        }
        return jwtActual().getClaimAsString("email");
    }

    private Jwt jwtActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad");
    }
}
