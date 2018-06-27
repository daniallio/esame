/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import model.Utente;

@Stateless
public class UtentiStore {

    //mettendo l'annotazione viene creato in automatico L'entitymanager
    @PersistenceContext
    EntityManager em;

//restituisce un lista di tutti glu user registrati
    public List<Utente> getAll() {
        return em.createNamedQuery(Utente.FIND_ALL, Utente.class).getResultList();
    }

//crea un utente in db solo se non è presente in db    
    public boolean crea(Utente u) {

        boolean esito = isRecordExist(u.getEmail());
        if (esito) {
            em.merge(u);
            return true;
        } else {
            return false;
        }

    }

    //metodo che verifica se data una mail è presente un record in db
    public boolean isRecordExist(String mail) {
        String query = "SELECT COUNT(u) From Utente u where u.email = :email";

        Long count = (Long) em.createQuery(query).setParameter("email", mail).getSingleResult();

        if (count == 0) {
            return true;
        } else {
            return false;
        }

    }

//metodo per verificare il login
//in ingresso riceve la mail e la password, verifica se sono presenti in database, se si genera il token e la dta di scadenza salvandoli 
//per l'utente, non ricevesse riscontro la query verrebbe raccolta l'eccezione e ritornano un Optional vuoto    
    public Optional<Utente> login(String email, String psw) {
        
        try{
            Utente u = em.createNamedQuery(Utente.FIND_BY_MAIL_PSW, Utente.class).
                    setParameter("email", email).
                    setParameter("psw", psw)
                    .getSingleResult();
            
            String token = UUID.randomUUID().toString();
            u.setToken(token);
            u.setTokenEnd(DateUtils.scadenzaToken(20));
           return Optional.of(u);
           
        }catch(NoResultException ex){
            return Optional.empty();
        }
        
        
       
    }
    
    
    public Optional<Utente> validate(String token) {
        System.out.println(token + "token in QUERY");
        try {
            Utente user = em.createNamedQuery(Utente.FIND_BY_TOKEN, Utente.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException ex) {
            return Optional.empty();
        }

    }
    

}
