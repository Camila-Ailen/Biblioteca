
package unam.biblioteca.model;


public class Copia {
    
    private int id;
    private boolean referencia;
    private enum tipo {
        TAPA_DURA, LIBRO_EN_RUSTICA, AUDIOLIBRO, LIBRO_ELECTRONICO
    }
    private enum estado {
        DISPONIBLE, PRESTADA, PERDIDA
    }
    
}
