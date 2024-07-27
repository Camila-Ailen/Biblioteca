
package unam.biblioteca.model;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class Copia {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    private boolean referencia;
    
    //enums
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    @Enumerated(EnumType.STRING)
    private Estado estado;
    
    //relaciones
    @ManyToOne
    private Rack idRack;
    
    @ManyToOne
    private Libro idLibro;
    
    @OneToMany (mappedBy = "idCopia")
    private ArrayList<Prestamo> listaPrestamos;
    
    //definiciones de los enums
    public enum Tipo {
        TAPA_DURA, LIBRO_EN_RUSTICA, AUDIOLIBRO, LIBRO_ELECTRONICO
    }
    public enum Estado {
        DISPONIBLE, PRESTADA, PERDIDA
    }
    
    //controladores, getters y setters
    
    
}
