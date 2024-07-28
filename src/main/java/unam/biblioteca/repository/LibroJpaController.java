
package unam.biblioteca.repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unam.biblioteca.model.Tematica;
import unam.biblioteca.model.Autor;
import unam.biblioteca.model.Idioma;
import unam.biblioteca.model.Editorial;
import unam.biblioteca.model.Copia;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unam.biblioteca.model.Libro;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;


public class LibroJpaController implements Serializable {

    public LibroJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public LibroJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Libro libro) {
        if (libro.getListaCopias() == null) {
            libro.setListaCopias(new ArrayList<Copia>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tematica idTematica = libro.getIdTematica();
            if (idTematica != null) {
                idTematica = em.getReference(idTematica.getClass(), idTematica.getId());
                libro.setIdTematica(idTematica);
            }
            Autor idAutor = libro.getIdAutor();
            if (idAutor != null) {
                idAutor = em.getReference(idAutor.getClass(), idAutor.getId());
                libro.setIdAutor(idAutor);
            }
            Idioma idIdioma = libro.getIdIdioma();
            if (idIdioma != null) {
                idIdioma = em.getReference(idIdioma.getClass(), idIdioma.getId());
                libro.setIdIdioma(idIdioma);
            }
            Editorial idEditorial = libro.getIdEditorial();
            if (idEditorial != null) {
                idEditorial = em.getReference(idEditorial.getClass(), idEditorial.getId());
                libro.setIdEditorial(idEditorial);
            }
            ArrayList<Copia> attachedListaCopias = new ArrayList<Copia>();
            for (Copia listaCopiasCopiaToAttach : libro.getListaCopias()) {
                listaCopiasCopiaToAttach = em.getReference(listaCopiasCopiaToAttach.getClass(), listaCopiasCopiaToAttach.getId());
                attachedListaCopias.add(listaCopiasCopiaToAttach);
            }
            libro.setListaCopias(attachedListaCopias);
            em.persist(libro);
            if (idTematica != null) {
                idTematica.getListaLibros().add(libro);
                idTematica = em.merge(idTematica);
            }
            if (idAutor != null) {
                idAutor.getListaLibros().add(libro);
                idAutor = em.merge(idAutor);
            }
            if (idIdioma != null) {
                idIdioma.getListaLibros().add(libro);
                idIdioma = em.merge(idIdioma);
            }
            if (idEditorial != null) {
                idEditorial.getListaLibros().add(libro);
                idEditorial = em.merge(idEditorial);
            }
            for (Copia listaCopiasCopia : libro.getListaCopias()) {
                Libro oldIdLibroOfListaCopiasCopia = listaCopiasCopia.getIdLibro();
                listaCopiasCopia.setIdLibro(libro);
                listaCopiasCopia = em.merge(listaCopiasCopia);
                if (oldIdLibroOfListaCopiasCopia != null) {
                    oldIdLibroOfListaCopiasCopia.getListaCopias().remove(listaCopiasCopia);
                    oldIdLibroOfListaCopiasCopia = em.merge(oldIdLibroOfListaCopiasCopia);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Libro libro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libro persistentLibro = em.find(Libro.class, libro.getId());
            Tematica idTematicaOld = persistentLibro.getIdTematica();
            Tematica idTematicaNew = libro.getIdTematica();
            Autor idAutorOld = persistentLibro.getIdAutor();
            Autor idAutorNew = libro.getIdAutor();
            Idioma idIdiomaOld = persistentLibro.getIdIdioma();
            Idioma idIdiomaNew = libro.getIdIdioma();
            Editorial idEditorialOld = persistentLibro.getIdEditorial();
            Editorial idEditorialNew = libro.getIdEditorial();
            ArrayList<Copia> listaCopiasOld = persistentLibro.getListaCopias();
            ArrayList<Copia> listaCopiasNew = libro.getListaCopias();
            if (idTematicaNew != null) {
                idTematicaNew = em.getReference(idTematicaNew.getClass(), idTematicaNew.getId());
                libro.setIdTematica(idTematicaNew);
            }
            if (idAutorNew != null) {
                idAutorNew = em.getReference(idAutorNew.getClass(), idAutorNew.getId());
                libro.setIdAutor(idAutorNew);
            }
            if (idIdiomaNew != null) {
                idIdiomaNew = em.getReference(idIdiomaNew.getClass(), idIdiomaNew.getId());
                libro.setIdIdioma(idIdiomaNew);
            }
            if (idEditorialNew != null) {
                idEditorialNew = em.getReference(idEditorialNew.getClass(), idEditorialNew.getId());
                libro.setIdEditorial(idEditorialNew);
            }
            ArrayList<Copia> attachedListaCopiasNew = new ArrayList<Copia>();
            for (Copia listaCopiasNewCopiaToAttach : listaCopiasNew) {
                listaCopiasNewCopiaToAttach = em.getReference(listaCopiasNewCopiaToAttach.getClass(), listaCopiasNewCopiaToAttach.getId());
                attachedListaCopiasNew.add(listaCopiasNewCopiaToAttach);
            }
            listaCopiasNew = attachedListaCopiasNew;
            libro.setListaCopias(listaCopiasNew);
            libro = em.merge(libro);
            if (idTematicaOld != null && !idTematicaOld.equals(idTematicaNew)) {
                idTematicaOld.getListaLibros().remove(libro);
                idTematicaOld = em.merge(idTematicaOld);
            }
            if (idTematicaNew != null && !idTematicaNew.equals(idTematicaOld)) {
                idTematicaNew.getListaLibros().add(libro);
                idTematicaNew = em.merge(idTematicaNew);
            }
            if (idAutorOld != null && !idAutorOld.equals(idAutorNew)) {
                idAutorOld.getListaLibros().remove(libro);
                idAutorOld = em.merge(idAutorOld);
            }
            if (idAutorNew != null && !idAutorNew.equals(idAutorOld)) {
                idAutorNew.getListaLibros().add(libro);
                idAutorNew = em.merge(idAutorNew);
            }
            if (idIdiomaOld != null && !idIdiomaOld.equals(idIdiomaNew)) {
                idIdiomaOld.getListaLibros().remove(libro);
                idIdiomaOld = em.merge(idIdiomaOld);
            }
            if (idIdiomaNew != null && !idIdiomaNew.equals(idIdiomaOld)) {
                idIdiomaNew.getListaLibros().add(libro);
                idIdiomaNew = em.merge(idIdiomaNew);
            }
            if (idEditorialOld != null && !idEditorialOld.equals(idEditorialNew)) {
                idEditorialOld.getListaLibros().remove(libro);
                idEditorialOld = em.merge(idEditorialOld);
            }
            if (idEditorialNew != null && !idEditorialNew.equals(idEditorialOld)) {
                idEditorialNew.getListaLibros().add(libro);
                idEditorialNew = em.merge(idEditorialNew);
            }
            for (Copia listaCopiasOldCopia : listaCopiasOld) {
                if (!listaCopiasNew.contains(listaCopiasOldCopia)) {
                    listaCopiasOldCopia.setIdLibro(null);
                    listaCopiasOldCopia = em.merge(listaCopiasOldCopia);
                }
            }
            for (Copia listaCopiasNewCopia : listaCopiasNew) {
                if (!listaCopiasOld.contains(listaCopiasNewCopia)) {
                    Libro oldIdLibroOfListaCopiasNewCopia = listaCopiasNewCopia.getIdLibro();
                    listaCopiasNewCopia.setIdLibro(libro);
                    listaCopiasNewCopia = em.merge(listaCopiasNewCopia);
                    if (oldIdLibroOfListaCopiasNewCopia != null && !oldIdLibroOfListaCopiasNewCopia.equals(libro)) {
                        oldIdLibroOfListaCopiasNewCopia.getListaCopias().remove(listaCopiasNewCopia);
                        oldIdLibroOfListaCopiasNewCopia = em.merge(oldIdLibroOfListaCopiasNewCopia);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = libro.getId();
                if (findLibro(id) == null) {
                    throw new NonexistentEntityException("The libro with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libro libro;
            try {
                libro = em.getReference(Libro.class, id);
                libro.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libro with id " + id + " no longer exists.", enfe);
            }
            Tematica idTematica = libro.getIdTematica();
            if (idTematica != null) {
                idTematica.getListaLibros().remove(libro);
                idTematica = em.merge(idTematica);
            }
            Autor idAutor = libro.getIdAutor();
            if (idAutor != null) {
                idAutor.getListaLibros().remove(libro);
                idAutor = em.merge(idAutor);
            }
            Idioma idIdioma = libro.getIdIdioma();
            if (idIdioma != null) {
                idIdioma.getListaLibros().remove(libro);
                idIdioma = em.merge(idIdioma);
            }
            Editorial idEditorial = libro.getIdEditorial();
            if (idEditorial != null) {
                idEditorial.getListaLibros().remove(libro);
                idEditorial = em.merge(idEditorial);
            }
            ArrayList<Copia> listaCopias = libro.getListaCopias();
            for (Copia listaCopiasCopia : listaCopias) {
                listaCopiasCopia.setIdLibro(null);
                listaCopiasCopia = em.merge(listaCopiasCopia);
            }
            em.remove(libro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Libro> findLibroEntities() {
        return findLibroEntities(true, -1, -1);
    }

    public List<Libro> findLibroEntities(int maxResults, int firstResult) {
        return findLibroEntities(false, maxResults, firstResult);
    }

    private List<Libro> findLibroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libro.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Libro findLibro(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Libro.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Libro> rt = cq.from(Libro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
