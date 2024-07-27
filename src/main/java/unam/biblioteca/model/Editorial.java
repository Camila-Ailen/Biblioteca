
package unam.biblioteca.model;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Editorial {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)  
    private int id;
    private String nombre;
    
    //relaciones
    @OneToMany (mappedBy = "idEditorial")
    private ArrayList<Libro> listaLibros;
    
}
