
package unam.biblioteca.model;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Rack implements Serializable {
    //atributos
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    private String descripcion;
    
    //relaciones
    @OneToMany (mappedBy = "idRack")
    private ArrayList<Copia> listaCopias;
    
    //controladores, getters y setters

    public Rack() {
    }

    public Rack(int id, String descripcion, ArrayList<Copia> listaCopias) {
        this.id = id;
        this.descripcion = descripcion;
        this.listaCopias = listaCopias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<Copia> getListaCopias() {
        return listaCopias;
    }

    public void setListaCopias(ArrayList<Copia> listaCopias) {
        this.listaCopias = listaCopias;
    }
    
    
}
