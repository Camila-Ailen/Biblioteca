
package unam.biblioteca.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Prestamo implements Serializable {
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
    @JoinColumn(name="id")
    private Miembro idMiembro;
    
    @ManyToOne
    @JoinColumn(name="id")
    private Copia idCopia;
    
    //controladores, getters y setters

    public Prestamo() {
    }

    public Prestamo(int id, Date fechaRetiro, Date fechaDevuelto, double multa, Miembro idMiembro, Copia idCopia) {
        this.id = id;
        this.fechaRetiro = fechaRetiro;
        this.fechaDevuelto = fechaDevuelto;
        this.multa = multa;
        this.idMiembro = idMiembro;
        this.idCopia = idCopia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public Date getFechaDevuelto() {
        return fechaDevuelto;
    }

    public void setFechaDevuelto(Date fechaDevuelto) {
        this.fechaDevuelto = fechaDevuelto;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public Miembro getIdMiembro() {
        return idMiembro;
    }

    public void setIdMiembro(Miembro idMiembro) {
        this.idMiembro = idMiembro;
    }

    public Copia getIdCopia() {
        return idCopia;
    }

    public void setIdCopia(Copia idCopia) {
        this.idCopia = idCopia;
    }
    
    
}
