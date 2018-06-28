package model;

import controller.DocumentoStore;
import controller.UtentiStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javafx.scene.media.Media;
import javax.inject.Inject;

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
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("/documenti")
@AuthRequired //interviene filtro
public class DocumentoRes {

    private static final String LOCATION = "/home/tss/Scrivania/";
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
    
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getDocUser(@PathParam("id") String id){
     return docStore.findUserDoc(id);
    
     }
    
//elenco dei docomenti di un user    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getId(@FormParam("id") String id){
        
        return docStore.findUserDoc(id);
    }
    
    
    //carico un nuovo documento per l'utente loggato
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart form) {
        try {

            FormDataBodyPart filePart = form.getField("file");
            ContentDisposition contentDispositionHeader = filePart.getContentDisposition();

            InputStream fileInputStream = filePart.getValueAs(InputStream.class);

            Files.copy(fileInputStream,
                    Paths.get(LOCATION + contentDispositionHeader.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);

            //recupero i valori dei parametri inviati via form
            //ID
            FormDataBodyPart id = form.getField("id");
            String contentID = id.getValue();
            //TItolo
            FormDataBodyPart titolo = form.getField("titolo");
            String contentTitolo = titolo.getValue();

            String path = (contentDispositionHeader.getFileName());
            path = path.trim();

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
                    Response.ok(Files.readAllBytes(Paths.get(LOCATION + fname)));
            rb.type(MediaType.APPLICATION_OCTET_STREAM);
            rb.header("Content-Disposition", "attachment; filename=\"" + fname + "\"");
            return rb.build();
        }catch(IOException ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }
    
    @DELETE
    @Path("/elimina/{id}")
    public Response delete(@PathParam("id") String id){
       try{
          String path; 
          path = docStore.deleteDoc(id);  
          Files.delete(Paths.get(LOCATION + path));
          String output = "File " + path + " eliminato correttamente.";
          return Response.ok(output).build();
       }catch(IOException ex) {
           return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
       }
       
        
    }
        
    
// condividi file   
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
    
    
    @GET
    @Path("/docCondivisi/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Documento> getDocCondivisi(@PathParam("id") String id){
        
        return docStore.findCondivisi1(id);
    }
}
