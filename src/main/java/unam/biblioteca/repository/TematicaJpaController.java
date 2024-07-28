
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
import unam.biblioteca.model.Tematica;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;


public class TematicaJpaController implements Serializable {

    public TematicaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public TematicaJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tematica tematica) {
        if (tematica.getListaLibros() == null) {
            tematica.setListaLibros(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArrayList<Libro> attachedListaLibros = new ArrayList<Libro>();
            for (Libro listaLibrosLibroToAttach : tematica.getListaLibros()) {
                listaLibrosLibroToAttach = em.getReference(listaLibrosLibroToAttach.getClass(), listaLibrosLibroToAttach.getId());
                attachedListaLibros.add(listaLibrosLibroToAttach);
            }
            tematica.setListaLibros(attachedListaLibros);
            em.persist(tematica);
            for (Libro listaLibrosLibro : tematica.getListaLibros()) {
                Tematica oldIdTematicaOfListaLibrosLibro = listaLibrosLibro.getIdTematica();
                listaLibrosLibro.setIdTematica(tematica);
                listaLibrosLibro = em.merge(listaLibrosLibro);
                if (oldIdTematicaOfListaLibrosLibro != null) {
                    oldIdTematicaOfListaLibrosLibro.getListaLibros().remove(listaLibrosLibro);
                    oldIdTematicaOfListaLibrosLibro = em.merge(oldIdTematicaOfListaLibrosLibro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tematica tematica) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tematica persistentTematica = em.find(Tematica.class, tematica.getId());
            ArrayList<Libro> listaLibrosOld = persistentTematica.getListaLibros();
            ArrayList<Libro> listaLibrosNew = tematica.getListaLibros();
            ArrayList<Libro> attachedListaLibrosNew = new ArrayList<Libro>();
            for (Libro listaLibrosNewLibroToAttach : listaLibrosNew) {
                listaLibrosNewLibroToAttach = em.getReference(listaLibrosNewLibroToAttach.getClass(), listaLibrosNewLibroToAttach.getId());
                attachedListaLibrosNew.add(listaLibrosNewLibroToAttach);
            }
            listaLibrosNew = attachedListaLibrosNew;
            tematica.setListaLibros(listaLibrosNew);
            tematica = em.merge(tematica);
            for (Libro listaLibrosOldLibro : listaLibrosOld) {
                if (!listaLibrosNew.contains(listaLibrosOldLibro)) {
                    listaLibrosOldLibro.setIdTematica(null);
                    listaLibrosOldLibro = em.merge(listaLibrosOldLibro);
                }
            }
            for (Libro listaLibrosNewLibro : listaLibrosNew) {
                if (!listaLibrosOld.contains(listaLibrosNewLibro)) {
                    Tematica oldIdTematicaOfListaLibrosNewLibro = listaLibrosNewLibro.getIdTematica();
                    listaLibrosNewLibro.setIdTematica(tematica);
                    listaLibrosNewLibro = em.merge(listaLibrosNewLibro);
                    if (oldIdTematicaOfListaLibrosNewLibro != null && !oldIdTematicaOfListaLibrosNewLibro.equals(tematica)) {
                        oldIdTematicaOfListaLibrosNewLibro.getListaLibros().remove(listaLibrosNewLibro);
                        oldIdTematicaOfListaLibrosNewLibro = em.merge(oldIdTematicaOfListaLibrosNewLibro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = tematica.getId();
                if (findTematica(id) == null) {
                    throw new NonexistentEntityException("The tematica with id " + id + " no longer exists.");
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
            Tematica tematica;
            try {
                tematica = em.getReference(Tematica.class, id);
                tematica.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tematica with id " + id + " no longer exists.", enfe);
            }
            ArrayList<Libro> listaLibros = tematica.getListaLibros();
            for (Libro listaLibrosLibro : listaLibros) {
                listaLibrosLibro.setIdTematica(null);
                listaLibrosLibro = em.merge(listaLibrosLibro);
            }
            em.remove(tematica);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tematica> findTematicaEntities() {
        return findTematicaEntities(true, -1, -1);
    }

    public List<Tematica> findTematicaEntities(int maxResults, int firstResult) {
        return findTematicaEntities(false, maxResults, firstResult);
    }

    private List<Tematica> findTematicaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tematica.class));
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

    public Tematica findTematica(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tematica.class, id);
        } finally {
            em.close();
        }
    }

    public int getTematicaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tematica> rt = cq.from(Tematica.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
