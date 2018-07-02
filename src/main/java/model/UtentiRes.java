/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import controller.UtentiStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/utenti")
public class UtentiRes {

    public static final String PATH_CLOUD = "/home/tss/Scrivania/cloud/";
    
    @Inject
    UtentiStore store;

    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Utente> allUsers(@PathParam("id") String id) {
        return store.getAll(id);
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
            
            try {
                //cerco l'utente appena creato per prendere l'id in maniera da creare una cartella univoca
                Utente utenteCartella = store.findByEmail(email);
                Files.createDirectories(Paths.get(PATH_CLOUD + utenteCartella.getId()));
            } catch (IOException ex) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
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
            //memorizzo l'id nella variabile di documentoRes
            DocumentoRes.setIdUtente(String.valueOf(logged.getId()));
            return Response.ok(json).build();
            
        } catch (EJBException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

    }
    
    
    
}
