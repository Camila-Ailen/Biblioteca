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
import unam.biblioteca.model.Rack;
import unam.biblioteca.model.Libro;
import unam.biblioteca.model.Prestamo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unam.biblioteca.model.Copia;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author camilaailen
 */
public class CopiaJpaController implements Serializable {

    public CopiaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public CopiaJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Copia copia) {
        if (copia.getListaPrestamos() == null) {
            copia.setListaPrestamos(new ArrayList<Prestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rack unRack = copia.getUnRack();
            if (unRack != null) {
                unRack = em.getReference(unRack.getClass(), unRack.getId());
                copia.setUnRack(unRack);
            }
            Libro unLibro = copia.getUnLibro();
            if (unLibro != null) {
                unLibro = em.getReference(unLibro.getClass(), unLibro.getId());
                copia.setUnLibro(unLibro);
            }
            ArrayList<Prestamo> attachedListaPrestamos = new ArrayList<Prestamo>();
            for (Prestamo listaPrestamosPrestamoToAttach : copia.getListaPrestamos()) {
                listaPrestamosPrestamoToAttach = em.getReference(listaPrestamosPrestamoToAttach.getClass(), listaPrestamosPrestamoToAttach.getId());
                attachedListaPrestamos.add(listaPrestamosPrestamoToAttach);
            }
            copia.setListaPrestamos(attachedListaPrestamos);
            em.persist(copia);
            if (unRack != null) {
                unRack.getListaCopias().add(copia);
                unRack = em.merge(unRack);
            }
            if (unLibro != null) {
                unLibro.getListaCopias().add(copia);
                unLibro = em.merge(unLibro);
            }
            for (Prestamo listaPrestamosPrestamo : copia.getListaPrestamos()) {
                Copia oldUnCopiaOfListaPrestamosPrestamo = listaPrestamosPrestamo.getUnCopia();
                listaPrestamosPrestamo.setUnCopia(copia);
                listaPrestamosPrestamo = em.merge(listaPrestamosPrestamo);
                if (oldUnCopiaOfListaPrestamosPrestamo != null) {
                    oldUnCopiaOfListaPrestamosPrestamo.getListaPrestamos().remove(listaPrestamosPrestamo);
                    oldUnCopiaOfListaPrestamosPrestamo = em.merge(oldUnCopiaOfListaPrestamosPrestamo);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Copia copia) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Copia persistentCopia = em.find(Copia.class, copia.getId());
            Rack unRackOld = persistentCopia.getUnRack();
            Rack unRackNew = copia.getUnRack();
            Libro unLibroOld = persistentCopia.getUnLibro();
            Libro unLibroNew = copia.getUnLibro();
            ArrayList<Prestamo> listaPrestamosOld = persistentCopia.getListaPrestamos();
            ArrayList<Prestamo> listaPrestamosNew = copia.getListaPrestamos();
            if (unRackNew != null) {
                unRackNew = em.getReference(unRackNew.getClass(), unRackNew.getId());
                copia.setUnRack(unRackNew);
            }
            if (unLibroNew != null) {
                unLibroNew = em.getReference(unLibroNew.getClass(), unLibroNew.getId());
                copia.setUnLibro(unLibroNew);
            }
            ArrayList<Prestamo> attachedListaPrestamosNew = new ArrayList<Prestamo>();
            for (Prestamo listaPrestamosNewPrestamoToAttach : listaPrestamosNew) {
                listaPrestamosNewPrestamoToAttach = em.getReference(listaPrestamosNewPrestamoToAttach.getClass(), listaPrestamosNewPrestamoToAttach.getId());
                attachedListaPrestamosNew.add(listaPrestamosNewPrestamoToAttach);
            }
            listaPrestamosNew = attachedListaPrestamosNew;
            copia.setListaPrestamos(listaPrestamosNew);
            copia = em.merge(copia);
            if (unRackOld != null && !unRackOld.equals(unRackNew)) {
                unRackOld.getListaCopias().remove(copia);
                unRackOld = em.merge(unRackOld);
            }
            if (unRackNew != null && !unRackNew.equals(unRackOld)) {
                unRackNew.getListaCopias().add(copia);
                unRackNew = em.merge(unRackNew);
            }
            if (unLibroOld != null && !unLibroOld.equals(unLibroNew)) {
                unLibroOld.getListaCopias().remove(copia);
                unLibroOld = em.merge(unLibroOld);
            }
            if (unLibroNew != null && !unLibroNew.equals(unLibroOld)) {
                unLibroNew.getListaCopias().add(copia);
                unLibroNew = em.merge(unLibroNew);
            }
            for (Prestamo listaPrestamosOldPrestamo : listaPrestamosOld) {
                if (!listaPrestamosNew.contains(listaPrestamosOldPrestamo)) {
                    listaPrestamosOldPrestamo.setUnCopia(null);
                    listaPrestamosOldPrestamo = em.merge(listaPrestamosOldPrestamo);
                }
            }
            for (Prestamo listaPrestamosNewPrestamo : listaPrestamosNew) {
                if (!listaPrestamosOld.contains(listaPrestamosNewPrestamo)) {
                    Copia oldUnCopiaOfListaPrestamosNewPrestamo = listaPrestamosNewPrestamo.getUnCopia();
                    listaPrestamosNewPrestamo.setUnCopia(copia);
                    listaPrestamosNewPrestamo = em.merge(listaPrestamosNewPrestamo);
                    if (oldUnCopiaOfListaPrestamosNewPrestamo != null && !oldUnCopiaOfListaPrestamosNewPrestamo.equals(copia)) {
                        oldUnCopiaOfListaPrestamosNewPrestamo.getListaPrestamos().remove(listaPrestamosNewPrestamo);
                        oldUnCopiaOfListaPrestamosNewPrestamo = em.merge(oldUnCopiaOfListaPrestamosNewPrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = copia.getId();
                if (findCopia(id) == null) {
                    throw new NonexistentEntityException("The copia with id " + id + " no longer exists.");
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
            Copia copia;
            try {
                copia = em.getReference(Copia.class, id);
                copia.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The copia with id " + id + " no longer exists.", enfe);
            }
            Rack unRack = copia.getUnRack();
            if (unRack != null) {
                unRack.getListaCopias().remove(copia);
                unRack = em.merge(unRack);
            }
            Libro unLibro = copia.getUnLibro();
            if (unLibro != null) {
                unLibro.getListaCopias().remove(copia);
                unLibro = em.merge(unLibro);
            }
            ArrayList<Prestamo> listaPrestamos = copia.getListaPrestamos();
            for (Prestamo listaPrestamosPrestamo : listaPrestamos) {
                listaPrestamosPrestamo.setUnCopia(null);
                listaPrestamosPrestamo = em.merge(listaPrestamosPrestamo);
            }
            em.remove(copia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Copia> findCopiaEntities() {
        return findCopiaEntities(true, -1, -1);
    }

    public List<Copia> findCopiaEntities(int maxResults, int firstResult) {
        return findCopiaEntities(false, maxResults, firstResult);
    }

    private List<Copia> findCopiaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Copia.class));
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

    public Copia findCopia(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Copia.class, id);
        } finally {
            em.close();
        }
    }

    public int getCopiaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Copia> rt = cq.from(Copia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
