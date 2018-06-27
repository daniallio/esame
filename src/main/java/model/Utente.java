/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="utenti")
@XmlRootElement      
@NamedQueries({
@NamedQuery(name = Utente.FIND_ALL, query = "SELECT u FROM Utente u where u.id != :id"),
@NamedQuery(name = Utente.FIND_BY_MAIL_PSW, query = "SELECT u FROM Utente u where u.email = :email and u.password = :psw"),
@NamedQuery(name = Utente.FIND_BY_ID, query = "SELECT u FROM Utente u where u.id = :id"),
@NamedQuery(name = Utente.FIND_BY_TOKEN, query = "SELECT u FROM Utente u where u.token = :token")        
})
        
        
public class Utente implements Serializable{
   
    public static final String FIND_ALL = "utente.findAll";
    public static final String FIND_BY_MAIL_PSW = "utente.findLogin";
    public static final String FIND_BY_ID = "Utente.findId";
    public static final String FIND_BY_TOKEN = "Utente.findToken";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, name ="email",nullable =false)
    private String email;
    
    @Column(name ="password",nullable =false)
    private String password;
    
    @Column(name ="token")
    private String token;
    
    @Column(name ="token_end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenEnd;
    
   

 
    
    public Utente() {
    }

    Utente(String email, String password) {
       this.email = email;
       this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

 

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenEnd() {
        return tokenEnd;
    }

    public void setTokenEnd(Date tokenEnd) {
        this.tokenEnd = tokenEnd;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Utente other = (Utente) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Utente{" + "id=" + id + ", email=" + email + ", password=" + password + ", token=" + token + ", tokenEnd=" + tokenEnd + '}';
    }

    
    

    
    
}
