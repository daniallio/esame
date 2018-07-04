package model;

import controller.DocumentoStore;
import controller.UtentiStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("/documenti")

public class DocumentoRes {

    private static final String LOCATION = "/home/tss/Scrivania/cloud/";
    //salvero la mail utente per effettuare le operazioni di salva ed elimina file
    private static String emailUtente;

    public static String getEmailUtente() {
        return emailUtente;
    }

    public static void setEmailUtente(String emailUtente) {
        DocumentoRes.emailUtente = emailUtente;
    }
    
 

    

    
    
    
    @Inject
    DocumentoStore docStore;

    @Inject
    UtentiStore uStore;
    
    //elenco di tutti i documenti 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getAll(){
        return docStore.findAll();
    }
    
    
//elenco di documenti per utente    
    @AuthRequired //interviene filtro
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getDocUser(@PathParam("id") String id){
     return docStore.findUserDoc(id);
    
     }
    
//elenco dei docomenti di un user   
    @AuthRequired
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getId(@FormParam("id") String id){
        
        return docStore.findUserDoc(id);
    }
    
    
    //carico un nuovo documento per l'utente loggato
    @AuthRequired
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart form) {
        try {
//recupero il file inviato inviato via form
            FormDataBodyPart filePart = form.getField("file");
//recupero il nome del file
            ContentDisposition contentDispositionHeader = filePart.getContentDisposition();
//inserisco il file all'interno dell'input stream
            InputStream fileInputStream = filePart.getValueAs(InputStream.class);


            //recupero i valori dei parametri inviati via form
            //ID
            FormDataBodyPart id = form.getField("id");
            String contentID = id.getValue();
            //TItolo
            FormDataBodyPart titolo = form.getField("titolo");
            String contentTitolo = titolo.getValue();
            //email che mi serve per indicare la cartella dove salvare il file
            FormDataBodyPart email = form.getField("email");
            String contentEmail = email.getValue();
            
            String path = (contentDispositionHeader.getFileName());
            path = path.trim();

            DocumentoRes.emailUtente = contentEmail;
           //copio il il file all'interno della cartella 
            Files.copy(fileInputStream,
                    Paths.get(LOCATION + emailUtente + "/"  + contentDispositionHeader.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING); 
            
            
            System.out.println(" - " + contentID + " - " + path + " - " + contentTitolo);

            docStore.inserisciDoc(contentID, path, contentTitolo);
            String output = "File saved to server..";
            return Response.ok(output).build();

        } catch (IOException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    
    @GET
    @Path("/download/{fname}")
    public Response download(@PathParam("fname") String fname){
        try{
            Response.ResponseBuilder rb = 
                    Response.ok(Files.readAllBytes(Paths.get(LOCATION + emailUtente + "/" + fname)));
            rb.type(MediaType.APPLICATION_OCTET_STREAM);
            rb.header("Content-Disposition", "attachment; filename=\"" + fname + "\"");
            return rb.build();
        }catch(IOException ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }
    
    @AuthRequired
    @DELETE
    @Path("/elimina/{id}")
    public Response delete(@PathParam("id") String id){
       try{
          //conterrà il nome del file 
          String path; 
          path = docStore.deleteDoc(id);  
          Files.delete(Paths.get(LOCATION + emailUtente + "/" + path));
          String output = "File " + path + " eliminato correttamente.";
          return Response.ok(output).build();
       }catch(IOException ex) {
           return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
       }
       
        
    }
        
    
// condividi file  
    @AuthRequired
    @POST
    @Path("/condividi")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response condividi(@FormParam("selUtente") String idUtente, @FormParam("selFile") String idDoc){
       
        //cerco l'utente per cui voglio condividere
        Utente u = uStore.findById(idUtente);
        //cerco il documento da condividere
        Documento doc = docStore.findById(idDoc);
        //aggiungo l'utente alla lista delle condivisioni
        doc.getCondivisioni().add(u);
        //aggiorno il documento dato che ho aggiunto un elemento alla lista delle condivisioni
        docStore.save(doc);
        return Response.ok().build();
    }
    
    
    //elimina un file condiviso 
    @AuthRequired
    @DELETE
    @Path("/elCondividi/{idUtente}/{idDoc}")
    public Response elCondividi(@PathParam("idUtente") String idUtente, @PathParam("idDoc") String idDoc){
       
        //cerco l'utente per cui voglio condividere
        Utente u = uStore.findById(idUtente);
        //cerco il documento da condividere
        Documento doc = docStore.findById(idDoc);
        //aggiungo l'utente alla lista delle condivisioni
        doc.getCondivisioni().remove(u);
        //aggiorno il documento dato che ho aggiunto un elemento alla lista delle condivisioni
        docStore.save(doc);
        return Response.ok().build();
    }
    
    
    
//      @GET
//    @Path("/docCondivisi/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getDocCondivisi(@PathParam("id") String id){
//        
//        JsonObject json = null;
//        JsonObject jsonArray = null;
//        List<Documento> elencoUtenti = docStore.findCondivisi1(id);
//        
//        for(int i = 0; i < elencoUtenti.size(); i++){
//            for(int j = 0; j < elencoUtenti.get(i).getCondivisioni().size(); j++){
//            json = Json.createObjectBuilder().
//                    add("condivisioni", Json.createArrayBuilder().
//                    add(Json.createObjectBuilder().        
//                    add("email", elencoUtenti.get(i).getCondivisioni().get(j).getEmail())
//                    .add("path", elencoUtenti.get(i).getPath())))
//                    .build();
//            
//            
//            }
//        }
//        
//        return Response.ok(json).build();
//        
//    }
    
    @AuthRequired
    @GET
    @Path("/docCondivisi/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getDocCondivisi(@PathParam("id") String id){
        
        return docStore.findCondivisi1(id);
    }
    
   // @AuthRequired
    @GET
    @Path("/docCondivisiConMe/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getDocFileCondivisiConME(@PathParam("id") String id){
        
        return docStore.findCondivisiConMe(id);
    }
    
    
}
