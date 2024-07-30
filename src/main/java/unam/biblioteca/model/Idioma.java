
package unam.biblioteca.model;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Idioma implements Serializable {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    private String nombre;
    
    //relaciones
    @OneToMany (mappedBy = "unIdioma")
    private ArrayList<Libro> listaLibros;
    
    //controladores, getters y setters

    public Idioma() {
    }

    public Idioma(int id, String nombre, ArrayList<Libro> listaLibros) {
        this.id = id;
        this.nombre = nombre;
        this.listaLibros = listaLibros;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Libro> getListaLibros() {
        return listaLibros;
    }

    public void setListaLibros(ArrayList<Libro> listaLibros) {
        this.listaLibros = listaLibros;
    }
    
    
    
}
