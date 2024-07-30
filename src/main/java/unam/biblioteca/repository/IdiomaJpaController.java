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
import unam.biblioteca.model.Libro;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unam.biblioteca.model.Idioma;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author camilaailen
 */
public class IdiomaJpaController implements Serializable {

    public IdiomaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public IdiomaJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Idioma idioma) {
        if (idioma.getListaLibros() == null) {
            idioma.setListaLibros(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArrayList<Libro> attachedListaLibros = new ArrayList<Libro>();
            for (Libro listaLibrosLibroToAttach : idioma.getListaLibros()) {
                listaLibrosLibroToAttach = em.getReference(listaLibrosLibroToAttach.getClass(), listaLibrosLibroToAttach.getId());
                attachedListaLibros.add(listaLibrosLibroToAttach);
            }
            idioma.setListaLibros(attachedListaLibros);
            em.persist(idioma);
            for (Libro listaLibrosLibro : idioma.getListaLibros()) {
                Idioma oldUnIdiomaOfListaLibrosLibro = listaLibrosLibro.getUnIdioma();
                listaLibrosLibro.setUnIdioma(idioma);
                listaLibrosLibro = em.merge(listaLibrosLibro);
                if (oldUnIdiomaOfListaLibrosLibro != null) {
                    oldUnIdiomaOfListaLibrosLibro.getListaLibros().remove(listaLibrosLibro);
                    oldUnIdiomaOfListaLibrosLibro = em.merge(oldUnIdiomaOfListaLibrosLibro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Idioma idioma) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Idioma persistentIdioma = em.find(Idioma.class, idioma.getId());
            ArrayList<Libro> listaLibrosOld = persistentIdioma.getListaLibros();
            ArrayList<Libro> listaLibrosNew = idioma.getListaLibros();
            ArrayList<Libro> attachedListaLibrosNew = new ArrayList<Libro>();
            for (Libro listaLibrosNewLibroToAttach : listaLibrosNew) {
                listaLibrosNewLibroToAttach = em.getReference(listaLibrosNewLibroToAttach.getClass(), listaLibrosNewLibroToAttach.getId());
                attachedListaLibrosNew.add(listaLibrosNewLibroToAttach);
            }
            listaLibrosNew = attachedListaLibrosNew;
            idioma.setListaLibros(listaLibrosNew);
            idioma = em.merge(idioma);
            for (Libro listaLibrosOldLibro : listaLibrosOld) {
                if (!listaLibrosNew.contains(listaLibrosOldLibro)) {
                    listaLibrosOldLibro.setUnIdioma(null);
                    listaLibrosOldLibro = em.merge(listaLibrosOldLibro);
                }
            }
            for (Libro listaLibrosNewLibro : listaLibrosNew) {
                if (!listaLibrosOld.contains(listaLibrosNewLibro)) {
                    Idioma oldUnIdiomaOfListaLibrosNewLibro = listaLibrosNewLibro.getUnIdioma();
                    listaLibrosNewLibro.setUnIdioma(idioma);
                    listaLibrosNewLibro = em.merge(listaLibrosNewLibro);
                    if (oldUnIdiomaOfListaLibrosNewLibro != null && !oldUnIdiomaOfListaLibrosNewLibro.equals(idioma)) {
                        oldUnIdiomaOfListaLibrosNewLibro.getListaLibros().remove(listaLibrosNewLibro);
                        oldUnIdiomaOfListaLibrosNewLibro = em.merge(oldUnIdiomaOfListaLibrosNewLibro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = idioma.getId();
                if (findIdioma(id) == null) {
                    throw new NonexistentEntityException("The idioma with id " + id + " no longer exists.");
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
            Idioma idioma;
            try {
                idioma = em.getReference(Idioma.class, id);
                idioma.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The idioma with id " + id + " no longer exists.", enfe);
            }
            ArrayList<Libro> listaLibros = idioma.getListaLibros();
            for (Libro listaLibrosLibro : listaLibros) {
                listaLibrosLibro.setUnIdioma(null);
                listaLibrosLibro = em.merge(listaLibrosLibro);
            }
            em.remove(idioma);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Idioma> findIdiomaEntities() {
        return findIdiomaEntities(true, -1, -1);
    }

    public List<Idioma> findIdiomaEntities(int maxResults, int firstResult) {
        return findIdiomaEntities(false, maxResults, firstResult);
    }

    private List<Idioma> findIdiomaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Idioma.class));
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

    public Idioma findIdioma(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Idioma.class, id);
        } finally {
            em.close();
        }
    }

    public int getIdiomaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Idioma> rt = cq.from(Idioma.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
