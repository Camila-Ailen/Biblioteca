
package unam.biblioteca.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Prestamo {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)  
    private int id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRetiro;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDevuelto;
    
    private double multa;
    
    //relaciones
    @ManyToOne
    private Miembro idMiembro;
    
    @ManyToOne
    private Copia idCopia;
    
}
