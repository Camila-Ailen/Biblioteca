
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
import unam.biblioteca.model.Editorial;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;


public class EditorialJpaController implements Serializable {

    public EditorialJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public EditorialJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Editorial editorial) {
        if (editorial.getListaLibros() == null) {
            editorial.setListaLibros(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArrayList<Libro> attachedListaLibros = new ArrayList<Libro>();
            for (Libro listaLibrosLibroToAttach : editorial.getListaLibros()) {
                listaLibrosLibroToAttach = em.getReference(listaLibrosLibroToAttach.getClass(), listaLibrosLibroToAttach.getId());
                attachedListaLibros.add(listaLibrosLibroToAttach);
            }
            editorial.setListaLibros(attachedListaLibros);
            em.persist(editorial);
            for (Libro listaLibrosLibro : editorial.getListaLibros()) {
                Editorial oldIdEditorialOfListaLibrosLibro = listaLibrosLibro.getIdEditorial();
                listaLibrosLibro.setIdEditorial(editorial);
                listaLibrosLibro = em.merge(listaLibrosLibro);
                if (oldIdEditorialOfListaLibrosLibro != null) {
                    oldIdEditorialOfListaLibrosLibro.getListaLibros().remove(listaLibrosLibro);
                    oldIdEditorialOfListaLibrosLibro = em.merge(oldIdEditorialOfListaLibrosLibro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Editorial editorial) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Editorial persistentEditorial = em.find(Editorial.class, editorial.getId());
            ArrayList<Libro> listaLibrosOld = persistentEditorial.getListaLibros();
            ArrayList<Libro> listaLibrosNew = editorial.getListaLibros();
            ArrayList<Libro> attachedListaLibrosNew = new ArrayList<Libro>();
            for (Libro listaLibrosNewLibroToAttach : listaLibrosNew) {
                listaLibrosNewLibroToAttach = em.getReference(listaLibrosNewLibroToAttach.getClass(), listaLibrosNewLibroToAttach.getId());
                attachedListaLibrosNew.add(listaLibrosNewLibroToAttach);
            }
            listaLibrosNew = attachedListaLibrosNew;
            editorial.setListaLibros(listaLibrosNew);
            editorial = em.merge(editorial);
            for (Libro listaLibrosOldLibro : listaLibrosOld) {
                if (!listaLibrosNew.contains(listaLibrosOldLibro)) {
                    listaLibrosOldLibro.setIdEditorial(null);
                    listaLibrosOldLibro = em.merge(listaLibrosOldLibro);
                }
            }
            for (Libro listaLibrosNewLibro : listaLibrosNew) {
                if (!listaLibrosOld.contains(listaLibrosNewLibro)) {
                    Editorial oldIdEditorialOfListaLibrosNewLibro = listaLibrosNewLibro.getIdEditorial();
                    listaLibrosNewLibro.setIdEditorial(editorial);
                    listaLibrosNewLibro = em.merge(listaLibrosNewLibro);
                    if (oldIdEditorialOfListaLibrosNewLibro != null && !oldIdEditorialOfListaLibrosNewLibro.equals(editorial)) {
                        oldIdEditorialOfListaLibrosNewLibro.getListaLibros().remove(listaLibrosNewLibro);
                        oldIdEditorialOfListaLibrosNewLibro = em.merge(oldIdEditorialOfListaLibrosNewLibro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = editorial.getId();
                if (findEditorial(id) == null) {
                    throw new NonexistentEntityException("The editorial with id " + id + " no longer exists.");
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
            Editorial editorial;
            try {
                editorial = em.getReference(Editorial.class, id);
                editorial.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The editorial with id " + id + " no longer exists.", enfe);
            }
            ArrayList<Libro> listaLibros = editorial.getListaLibros();
            for (Libro listaLibrosLibro : listaLibros) {
                listaLibrosLibro.setIdEditorial(null);
                listaLibrosLibro = em.merge(listaLibrosLibro);
            }
            em.remove(editorial);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Editorial> findEditorialEntities() {
        return findEditorialEntities(true, -1, -1);
    }

    public List<Editorial> findEditorialEntities(int maxResults, int firstResult) {
        return findEditorialEntities(false, maxResults, firstResult);
    }

    private List<Editorial> findEditorialEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Editorial.class));
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

    public Editorial findEditorial(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Editorial.class, id);
        } finally {
            em.close();
        }
    }

    public int getEditorialCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Editorial> rt = cq.from(Editorial.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
