/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.UtentiStore;
import java.util.List;
import java.util.Optional;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/utenti")
public class UtentiRes {

    @Inject
    UtentiStore store;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Utente> allUsers() {
        return store.getAll();
    }

//registrati    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("rEmail") String email, @FormParam("rPsw") String password) {
        boolean esito = false;
        Utente u;
        u = new Utente(email, password);

        esito = store.crea(u);
        if (esito) {
            return Response.ok("Utente correttamente registrato").build();

        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

//login    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(Utente u) {
        try {
            Optional<Utente> opt = store.login(u.getEmail(), u.getPassword());
            Utente logged = opt.orElseThrow(() -> {
                return new EJBException("login failed");
            });

            JsonObject json = Json.createObjectBuilder().add("token", logged.getToken())
                    .add("email", logged.getEmail())
                    .add("id", logged.getId())
                    .build();
            return Response.ok(json).build();
            
        } catch (EJBException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

    }
    
    
    
}
