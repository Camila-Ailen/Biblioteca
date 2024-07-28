
package unam.biblioteca.repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unam.biblioteca.model.Libro;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unam.biblioteca.model.Autor;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;


public class AutorJpaController implements Serializable {

    public AutorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public AutorJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Autor autor) {
        if (autor.getListaLibros() == null) {
            autor.setListaLibros(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArrayList<Libro> attachedListaLibros = new ArrayList<Libro>();
            for (Libro listaLibrosLibroToAttach : autor.getListaLibros()) {
                listaLibrosLibroToAttach = em.getReference(listaLibrosLibroToAttach.getClass(), listaLibrosLibroToAttach.getId());
                attachedListaLibros.add(listaLibrosLibroToAttach);
            }
            autor.setListaLibros(attachedListaLibros);
            em.persist(autor);
            for (Libro listaLibrosLibro : autor.getListaLibros()) {
                Autor oldIdAutorOfListaLibrosLibro = listaLibrosLibro.getIdAutor();
                listaLibrosLibro.setIdAutor(autor);
                listaLibrosLibro = em.merge(listaLibrosLibro);
                if (oldIdAutorOfListaLibrosLibro != null) {
                    oldIdAutorOfListaLibrosLibro.getListaLibros().remove(listaLibrosLibro);
                    oldIdAutorOfListaLibrosLibro = em.merge(oldIdAutorOfListaLibrosLibro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Autor autor) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Autor persistentAutor = em.find(Autor.class, autor.getId());
            ArrayList<Libro> listaLibrosOld = persistentAutor.getListaLibros();
            ArrayList<Libro> listaLibrosNew = autor.getListaLibros();
            ArrayList<Libro> attachedListaLibrosNew = new ArrayList<Libro>();
            for (Libro listaLibrosNewLibroToAttach : listaLibrosNew) {
                listaLibrosNewLibroToAttach = em.getReference(listaLibrosNewLibroToAttach.getClass(), listaLibrosNewLibroToAttach.getId());
                attachedListaLibrosNew.add(listaLibrosNewLibroToAttach);
            }
            listaLibrosNew = attachedListaLibrosNew;
            autor.setListaLibros(listaLibrosNew);
            autor = em.merge(autor);
            for (Libro listaLibrosOldLibro : listaLibrosOld) {
                if (!listaLibrosNew.contains(listaLibrosOldLibro)) {
                    listaLibrosOldLibro.setIdAutor(null);
                    listaLibrosOldLibro = em.merge(listaLibrosOldLibro);
                }
            }
            for (Libro listaLibrosNewLibro : listaLibrosNew) {
                if (!listaLibrosOld.contains(listaLibrosNewLibro)) {
                    Autor oldIdAutorOfListaLibrosNewLibro = listaLibrosNewLibro.getIdAutor();
                    listaLibrosNewLibro.setIdAutor(autor);
                    listaLibrosNewLibro = em.merge(listaLibrosNewLibro);
                    if (oldIdAutorOfListaLibrosNewLibro != null && !oldIdAutorOfListaLibrosNewLibro.equals(autor)) {
                        oldIdAutorOfListaLibrosNewLibro.getListaLibros().remove(listaLibrosNewLibro);
                        oldIdAutorOfListaLibrosNewLibro = em.merge(oldIdAutorOfListaLibrosNewLibro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = autor.getId();
                if (findAutor(id) == null) {
                    throw new NonexistentEntityException("The autor with id " + id + " no longer exists.");
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
            Autor autor;
            try {
                autor = em.getReference(Autor.class, id);
                autor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autor with id " + id + " no longer exists.", enfe);
            }
            ArrayList<Libro> listaLibros = autor.getListaLibros();
            for (Libro listaLibrosLibro : listaLibros) {
                listaLibrosLibro.setIdAutor(null);
                listaLibrosLibro = em.merge(listaLibrosLibro);
            }
            em.remove(autor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Autor> findAutorEntities() {
        return findAutorEntities(true, -1, -1);
    }

    public List<Autor> findAutorEntities(int maxResults, int firstResult) {
        return findAutorEntities(false, maxResults, firstResult);
    }

    private List<Autor> findAutorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Autor.class));
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

    public Autor findAutor(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Autor.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Autor> rt = cq.from(Autor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
