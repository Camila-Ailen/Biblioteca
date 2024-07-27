
package unam.biblioteca.model;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Rack {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    private String descripcion;
    
    //relaciones
    @OneToMany (mappedBy = "idRack")
    private ArrayList<Copia> listaCopias;
    
    //controladores, getters y setters
    
    
}
