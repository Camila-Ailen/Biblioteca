
package unam.biblioteca.repository;

import java.util.List;
import unam.biblioteca.model.Miembro;
import unam.biblioteca.repository.*;


public class ControladoraPersistencia {
    
    AutorJpaController autorJpa = new AutorJpaController();
    CopiaJpaController copiaJpa = new CopiaJpaController();
    EditorialJpaController editorialJpa = new EditorialJpaController();
    IdiomaJpaController idiomaJpa = new IdiomaJpaController();
    LibroJpaController libroJpa = new LibroJpaController();
    MiembroJpaController miembroJpa = new MiembroJpaController();
    PrestamoJpaController prestamoJpa = new PrestamoJpaController();
    RackJpaController rackJpa = new RackJpaController();
    RolJpaController rolJpa = new RolJpaController();
    TematicaJpaController tematicaJpa = new TematicaJpaController();


    public List<Miembro> traerMiembros() {
        return miembroJpa.findMiembroEntities();
    }
    
}
