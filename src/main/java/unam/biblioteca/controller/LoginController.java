package unam.biblioteca.controller;

import java.util.List;
import unam.biblioteca.model.Miembro;
import unam.biblioteca.repository.ControladoraPersistencia;

public class LoginController {
    
    ControladoraPersistencia controlPersistencia;

    public LoginController() {
        controlPersistencia = new ControladoraPersistencia ();
    }

    
    
    public Miembro validarUsuario(String user, String pass) {
        
        Miembro usuario = null;
        
        List<Miembro> listaMiembros = controlPersistencia.traerMiembros();
        
        for (Miembro miembro : listaMiembros) {
            if (String.valueOf(miembro.getId()).equals(user)) {
                if (miembro.getClave().equals(pass)) {
                    usuario = miembro;
                    return usuario;
                } else  {
                    usuario = null;
                    return usuario;
                }
            } else {
                usuario = null;
            }
        }
        return usuario;
    }



  
    
}
