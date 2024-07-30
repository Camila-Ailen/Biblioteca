/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author camilaailen
 */
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
            Tematica unTematica = libro.getUnTematica();
            if (unTematica != null) {
                unTematica = em.getReference(unTematica.getClass(), unTematica.getId());
                libro.setUnTematica(unTematica);
            }
            Autor unAutor = libro.getUnAutor();
            if (unAutor != null) {
                unAutor = em.getReference(unAutor.getClass(), unAutor.getId());
                libro.setUnAutor(unAutor);
            }
            Idioma unIdioma = libro.getUnIdioma();
            if (unIdioma != null) {
                unIdioma = em.getReference(unIdioma.getClass(), unIdioma.getId());
                libro.setUnIdioma(unIdioma);
            }
            Editorial unEditorial = libro.getUnEditorial();
            if (unEditorial != null) {
                unEditorial = em.getReference(unEditorial.getClass(), unEditorial.getId());
                libro.setUnEditorial(unEditorial);
            }
            ArrayList<Copia> attachedListaCopias = new ArrayList<Copia>();
            for (Copia listaCopiasCopiaToAttach : libro.getListaCopias()) {
                listaCopiasCopiaToAttach = em.getReference(listaCopiasCopiaToAttach.getClass(), listaCopiasCopiaToAttach.getId());
                attachedListaCopias.add(listaCopiasCopiaToAttach);
            }
            libro.setListaCopias(attachedListaCopias);
            em.persist(libro);
            if (unTematica != null) {
                unTematica.getListaLibros().add(libro);
                unTematica = em.merge(unTematica);
            }
            if (unAutor != null) {
                unAutor.getListaLibros().add(libro);
                unAutor = em.merge(unAutor);
            }
            if (unIdioma != null) {
                unIdioma.getListaLibros().add(libro);
                unIdioma = em.merge(unIdioma);
            }
            if (unEditorial != null) {
                unEditorial.getListaLibros().add(libro);
                unEditorial = em.merge(unEditorial);
            }
            for (Copia listaCopiasCopia : libro.getListaCopias()) {
                Libro oldUnLibroOfListaCopiasCopia = listaCopiasCopia.getUnLibro();
                listaCopiasCopia.setUnLibro(libro);
                listaCopiasCopia = em.merge(listaCopiasCopia);
                if (oldUnLibroOfListaCopiasCopia != null) {
                    oldUnLibroOfListaCopiasCopia.getListaCopias().remove(listaCopiasCopia);
                    oldUnLibroOfListaCopiasCopia = em.merge(oldUnLibroOfListaCopiasCopia);
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
            Tematica unTematicaOld = persistentLibro.getUnTematica();
            Tematica unTematicaNew = libro.getUnTematica();
            Autor unAutorOld = persistentLibro.getUnAutor();
            Autor unAutorNew = libro.getUnAutor();
            Idioma unIdiomaOld = persistentLibro.getUnIdioma();
            Idioma unIdiomaNew = libro.getUnIdioma();
            Editorial unEditorialOld = persistentLibro.getUnEditorial();
            Editorial unEditorialNew = libro.getUnEditorial();
            ArrayList<Copia> listaCopiasOld = persistentLibro.getListaCopias();
            ArrayList<Copia> listaCopiasNew = libro.getListaCopias();
            if (unTematicaNew != null) {
                unTematicaNew = em.getReference(unTematicaNew.getClass(), unTematicaNew.getId());
                libro.setUnTematica(unTematicaNew);
            }
            if (unAutorNew != null) {
                unAutorNew = em.getReference(unAutorNew.getClass(), unAutorNew.getId());
                libro.setUnAutor(unAutorNew);
            }
            if (unIdiomaNew != null) {
                unIdiomaNew = em.getReference(unIdiomaNew.getClass(), unIdiomaNew.getId());
                libro.setUnIdioma(unIdiomaNew);
            }
            if (unEditorialNew != null) {
                unEditorialNew = em.getReference(unEditorialNew.getClass(), unEditorialNew.getId());
                libro.setUnEditorial(unEditorialNew);
            }
            ArrayList<Copia> attachedListaCopiasNew = new ArrayList<Copia>();
            for (Copia listaCopiasNewCopiaToAttach : listaCopiasNew) {
                listaCopiasNewCopiaToAttach = em.getReference(listaCopiasNewCopiaToAttach.getClass(), listaCopiasNewCopiaToAttach.getId());
                attachedListaCopiasNew.add(listaCopiasNewCopiaToAttach);
            }
            listaCopiasNew = attachedListaCopiasNew;
            libro.setListaCopias(listaCopiasNew);
            libro = em.merge(libro);
            if (unTematicaOld != null && !unTematicaOld.equals(unTematicaNew)) {
                unTematicaOld.getListaLibros().remove(libro);
                unTematicaOld = em.merge(unTematicaOld);
            }
            if (unTematicaNew != null && !unTematicaNew.equals(unTematicaOld)) {
                unTematicaNew.getListaLibros().add(libro);
                unTematicaNew = em.merge(unTematicaNew);
            }
            if (unAutorOld != null && !unAutorOld.equals(unAutorNew)) {
                unAutorOld.getListaLibros().remove(libro);
                unAutorOld = em.merge(unAutorOld);
            }
            if (unAutorNew != null && !unAutorNew.equals(unAutorOld)) {
                unAutorNew.getListaLibros().add(libro);
                unAutorNew = em.merge(unAutorNew);
            }
            if (unIdiomaOld != null && !unIdiomaOld.equals(unIdiomaNew)) {
                unIdiomaOld.getListaLibros().remove(libro);
                unIdiomaOld = em.merge(unIdiomaOld);
            }
            if (unIdiomaNew != null && !unIdiomaNew.equals(unIdiomaOld)) {
                unIdiomaNew.getListaLibros().add(libro);
                unIdiomaNew = em.merge(unIdiomaNew);
            }
            if (unEditorialOld != null && !unEditorialOld.equals(unEditorialNew)) {
                unEditorialOld.getListaLibros().remove(libro);
                unEditorialOld = em.merge(unEditorialOld);
            }
            if (unEditorialNew != null && !unEditorialNew.equals(unEditorialOld)) {
                unEditorialNew.getListaLibros().add(libro);
                unEditorialNew = em.merge(unEditorialNew);
            }
            for (Copia listaCopiasOldCopia : listaCopiasOld) {
                if (!listaCopiasNew.contains(listaCopiasOldCopia)) {
                    listaCopiasOldCopia.setUnLibro(null);
                    listaCopiasOldCopia = em.merge(listaCopiasOldCopia);
                }
            }
            for (Copia listaCopiasNewCopia : listaCopiasNew) {
                if (!listaCopiasOld.contains(listaCopiasNewCopia)) {
                    Libro oldUnLibroOfListaCopiasNewCopia = listaCopiasNewCopia.getUnLibro();
                    listaCopiasNewCopia.setUnLibro(libro);
                    listaCopiasNewCopia = em.merge(listaCopiasNewCopia);
                    if (oldUnLibroOfListaCopiasNewCopia != null && !oldUnLibroOfListaCopiasNewCopia.equals(libro)) {
                        oldUnLibroOfListaCopiasNewCopia.getListaCopias().remove(listaCopiasNewCopia);
                        oldUnLibroOfListaCopiasNewCopia = em.merge(oldUnLibroOfListaCopiasNewCopia);
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
            Tematica unTematica = libro.getUnTematica();
            if (unTematica != null) {
                unTematica.getListaLibros().remove(libro);
                unTematica = em.merge(unTematica);
            }
            Autor unAutor = libro.getUnAutor();
            if (unAutor != null) {
                unAutor.getListaLibros().remove(libro);
                unAutor = em.merge(unAutor);
            }
            Idioma unIdioma = libro.getUnIdioma();
            if (unIdioma != null) {
                unIdioma.getListaLibros().remove(libro);
                unIdioma = em.merge(unIdioma);
            }
            Editorial unEditorial = libro.getUnEditorial();
            if (unEditorial != null) {
                unEditorial.getListaLibros().remove(libro);
                unEditorial = em.merge(unEditorial);
            }
            ArrayList<Copia> listaCopias = libro.getListaCopias();
            for (Copia listaCopiasCopia : listaCopias) {
                listaCopiasCopia.setUnLibro(null);
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
