/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Documento;
import model.Utente;


@Stateless
public class DocumentoStore {
    
    @PersistenceContext
    EntityManager em;
    
//metodo che scrive nella tabella documenti    
public void inserisciDoc(String id, String path, String titolo){
   
    int idInt = Integer.parseInt(id);
    Utente u;
    u = em.createNamedQuery(Utente.FIND_BY_ID, Utente.class).setParameter("id", idInt).getSingleResult();
    Documento d = new Documento(titolo, path, u);
    em.merge(d);
    
}

//cerca documento per id
    public List<Documento> findAll(String id) {
        Long idUtente = Long.parseLong(id);
        return em.createNamedQuery(Documento.FIND_ALL_By_ID, Documento.class).setParameter("id", idUtente).getResultList();
    }

    
//cancella documento     
    public String deleteDoc(String id) {
        Documento doc;
        Long idDocumento = Long.parseLong(id);
        doc = em.find(Documento.class, idDocumento);
        String path = doc.getPath();
        em.remove(doc);
        return path;
    }

    public List<Documento> findAll() {
        return em.createNamedQuery(Documento.FIND_ALL, Documento.class).getResultList();
    }
   
}
