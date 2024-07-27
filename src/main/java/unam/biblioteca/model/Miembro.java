
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
public class Miembro {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)  
    private int id;
    private String clave;
    private String apellido;
    private String nombre;
    private String telefono;
    private String email;
    
    //enums
    @Enumerated(EnumType.STRING)
    private Estado estado;

    //relaciones
    @OneToMany (mappedBy = "idMiembro")
    private ArrayList<Prestamo> listaPrestamos;
    
    @ManyToOne
    private Rol idRol;
    
    //definiciones de los enum
    public enum Estado {
        ACTIVO, INACTIVO
    }
    
}
