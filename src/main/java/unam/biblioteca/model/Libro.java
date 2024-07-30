
package unam.biblioteca.model;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Libro implements Serializable {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    private String titulo;
    private String isbn;
    private double precio;
    
    //relaciones
    @ManyToOne
    @JoinColumn(name="id")
    private Tematica idTematica;
    
    @ManyToOne
    @JoinColumn(name="id")
    private Autor idAutor;
    
    @ManyToOne
    @JoinColumn(name="id")
    private Idioma idIdioma;
    
    @ManyToOne
    @JoinColumn(name="id")
    private Editorial idEditorial;
    
    @OneToMany (mappedBy = "idLibro")
    private ArrayList<Copia> listaCopias;
    
    //controladores, getters y setters

    public Libro() {
    }

    public Libro(int id, String titulo, String isbn, double precio, Tematica idTematica, Autor idAutor, Idioma idIdioma, Editorial idEditorial, ArrayList<Copia> listaCopias) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.precio = precio;
        this.idTematica = idTematica;
        this.idAutor = idAutor;
        this.idIdioma = idIdioma;
        this.idEditorial = idEditorial;
        this.listaCopias = listaCopias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Tematica getIdTematica() {
        return idTematica;
    }

    public void setIdTematica(Tematica idTematica) {
        this.idTematica = idTematica;
    }

    public Autor getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Autor idAutor) {
        this.idAutor = idAutor;
    }

    public Idioma getIdIdioma() {
        return idIdioma;
    }

    public void setIdIdioma(Idioma idIdioma) {
        this.idIdioma = idIdioma;
    }

    public Editorial getIdEditorial() {
        return idEditorial;
    }

    public void setIdEditorial(Editorial idEditorial) {
        this.idEditorial = idEditorial;
    }

    public ArrayList<Copia> getListaCopias() {
        return listaCopias;
    }

    public void setListaCopias(ArrayList<Copia> listaCopias) {
        this.listaCopias = listaCopias;
    }
    
    
    
    
}
