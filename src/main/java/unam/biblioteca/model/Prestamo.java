
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
    @JoinColumn(name="fk_miembro")
    private Miembro unMiembro;
    
    @ManyToOne
    @JoinColumn(name="fk_copia")
    private Copia unCopia;
    
    //controladores, getters y setters

    public Prestamo() {
    }

    public Prestamo(int id, Date fechaRetiro, Date fechaDevuelto, double multa, Miembro unMiembro, Copia unCopia) {
        this.id = id;
        this.fechaRetiro = fechaRetiro;
        this.fechaDevuelto = fechaDevuelto;
        this.multa = multa;
        this.unMiembro = unMiembro;
        this.unCopia = unCopia;
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

    public Miembro getUnMiembro() {
        return unMiembro;
    }

    public void setUnMiembro(Miembro unMiembro) {
        this.unMiembro = unMiembro;
    }

    public Copia getUnCopia() {
        return unCopia;
    }

    public void setUnCopia(Copia unCopia) {
        this.unCopia = unCopia;
    }

    
    
    
}
