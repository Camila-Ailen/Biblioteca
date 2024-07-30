package unam.biblioteca.controller;

import java.util.List;
import unam.biblioteca.model.Miembro;
import unam.biblioteca.repository.ControladoraPersistencia;

public class ControladoraLogica {
    
    ControladoraPersistencia controlPersistencia;

    public ControladoraLogica() {
        controlPersistencia = new ControladoraPersistencia ();
    }

    
    
    public String validarUsuario(String user, String pass) {
        String mensaje="";
        List<Miembro> listaMiembros = controlPersistencia.traerMiembros();
        
        for (Miembro miembro : listaMiembros) {
            if (miembro.getNombre().equals(user)) {
                if (miembro.getClave().equals(pass)) {
                    mensaje = "Bienvenido";
                    return mensaje;
                } else  {
                    mensaje = "Clave incorrecta";
                    return mensaje;
                }
            } else {
                mensaje = "No encontrado";
            }
        }
        return mensaje;
    }
    
}
