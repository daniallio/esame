/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Documento;
import model.Utente;


@Stateless
public class DocumentoStore {
    
    @PersistenceContext
    EntityManager em;
    
    
public void inserisciDoc(String id, String path, String titolo){
   
    int idInt = Integer.parseInt(id);
    Utente u;
    u = em.createNamedQuery(Utente.FIND_BY_ID, Utente.class).setParameter("id", idInt).getSingleResult();
    Documento d = new Documento(titolo, path, u);
    em.merge(d);
    
}
   
}
