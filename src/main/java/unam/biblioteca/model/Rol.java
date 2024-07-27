
package unam.biblioteca.model;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Rol {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)  
    private int id;
    private String nombre;
    
    //relaciones
    @OneToMany (mappedBy = "idRol")
    private ArrayList<Miembro> listaMiembros;
    
    //controladores, getters y setters

    public Rol() {
    }

    public Rol(int id, String nombre, ArrayList<Miembro> listaMiembros) {
        this.id = id;
        this.nombre = nombre;
        this.listaMiembros = listaMiembros;
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

    public ArrayList<Miembro> getListaMiembros() {
        return listaMiembros;
    }

    public void setListaMiembros(ArrayList<Miembro> listaMiembros) {
        this.listaMiembros = listaMiembros;
    }
    
    
}
