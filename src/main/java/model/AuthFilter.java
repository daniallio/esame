package model;

import controller.UtentiStore;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@AuthRequired //annotazione creata da noi ch andrà messa nei metodi dove sarà necessaria l'autenticazione
@Provider
@Priority(Priorities.AUTHENTICATION)//primo livello di 

public class AuthFilter implements ContainerRequestFilter {

    @Inject //inject istanzia in automatica l'oggetto
    UtentiStore Ustore;
    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext rc) throws IOException {

        System.out.println("controllo autenticazione");

        // Get the Authorization header from the request
        String authorizationHeader
                = rc.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(rc);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {

            // Validate the token
            validateToken(token);

        } catch (Exception e) {
            abortWithUnauthorized(rc);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    private void validateToken(String token) throws Exception {
        System.out.println("Entro nel validate");
        

        Optional<Utente> opt = Ustore.validate(token);

        Utente logged = opt.orElseThrow(() -> {
            return new IllegalArgumentException("Token inesistente");
        });

        if (logged.getTokenEnd().before(new Date())) {
            throw new IllegalArgumentException("Token scaduto");
        }

    }
}
