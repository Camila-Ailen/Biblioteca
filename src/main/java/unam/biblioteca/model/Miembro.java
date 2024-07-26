
package unam.biblioteca.model;

public class Miembro {
    
    private int id;
    private String clave;
    private String apellido;
    private String nombre;
    private String telefono;
    private String email;
    private enum estado {
        ACTIVO, INACTIVO
    }
    
}
