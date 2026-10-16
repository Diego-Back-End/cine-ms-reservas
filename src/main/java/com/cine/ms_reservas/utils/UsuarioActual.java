package com.cine.ms_reservas.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Unico lugar donde se obtiene el usuario autenticado.
 * Ningun controller ni service debe leer el token directamente: todos deben
 * depender de esta clase.
 *
 * <ul>
 *   <li>Perfil normal: lee los claims "sub" y "email" del JWT. El header
 *       X-Dev-Email se ignora por completo.</li>
 *   <li>Perfil dev: si llega el header X-Dev-Email con un correo valido se usa
 *       (en minusculas) como email y como usuarioId; si no llega o es
 *       invalido, se usa app.dev.email y el usuario fijo de dev.</li>
 * </ul>
 */
@Component
public class UsuarioActual {

    private static final String DEV_USUARIO_ID = "usuario-dev";
    private static final String DEV_EMAIL_HEADER = "X-Dev-Email";
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final boolean devProfile;
    private final String devEmail;

    public UsuarioActual(Environment environment,
                         @Value("${app.dev.email:}") String devEmail) {
        this.devProfile = environment.acceptsProfiles(Profiles.of("dev"));
        this.devEmail = devEmail;
    }

    public String getUsuarioId() {
        if (!devProfile) {
            Jwt jwt = jwtActual();
            String sub = jwt.getSubject();
            if (sub != null && !sub.isBlank()) {
                return sub;
            }
            return jwt.getClaimAsString("oid");
        }
        String emailDelHeader = devEmailDelHeader();
        return emailDelHeader != null ? emailDelHeader : DEV_USUARIO_ID;
    }

    public String getEmail() {
        if (!devProfile) {
            return jwtActual().getClaimAsString("email");
        }
        String emailDelHeader = devEmailDelHeader();
        return emailDelHeader != null ? emailDelHeader : devEmail;
    }

    /**
     * Solo en perfil dev: devuelve el email del header X-Dev-Email en
     * minusculas si tiene formato valido; en cualquier otro caso devuelve null
     * (y el llamador cae al valor por defecto de dev).
     */
    private String devEmailDelHeader() {
        if (!devProfile) {
            return null;
        }
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        String value = attributes.getRequest().getHeader(DEV_EMAIL_HEADER);
        if (value == null) {
            return null;
        }
        String correo = value.trim().toLowerCase(Locale.ROOT);
        if (correo.isEmpty() || !EMAIL_PATTERN.matcher(correo).matches()) {
            return null;
        }
        return correo;
    }

    private Jwt jwtActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad");
    }
}
