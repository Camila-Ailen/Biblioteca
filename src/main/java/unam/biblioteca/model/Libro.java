
package unam.biblioteca.model;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Libro {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    private String titulo;
    private String isbn;
    private double precio;
    
    //relaciones
    @ManyToOne
    private Tematica idTematica;
    
    @ManyToOne
    private Autor idAutor;
    
    @ManyToOne
    private Idioma idIdioma;
    
    @ManyToOne
    private Editorial idEditorial;
    
    @OneToMany (mappedBy = "idLibro")
    private ArrayList<Copia> listaCopias;
    
    
}
