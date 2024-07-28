
package unam.biblioteca.model;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Autor implements Serializable {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)  
    private int id;
    private String nombre;
    private String apellido;
    
    //relaciones
    @OneToMany (mappedBy = "idAutor")
    private ArrayList<Libro> listaLibros;
    
    //controladores, getters y setters

    public Autor() {
    }

    public Autor(int id, String nombre, String apellido, ArrayList<Libro> listaLibros) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public ArrayList<Libro> getListaLibros() {
        return listaLibros;
    }

    public void setListaLibros(ArrayList<Libro> listaLibros) {
        this.listaLibros = listaLibros;
    }
    
    
    
}
