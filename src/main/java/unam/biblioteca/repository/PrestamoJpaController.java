/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unam.biblioteca.repository;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unam.biblioteca.model.Miembro;
import unam.biblioteca.model.Copia;
import unam.biblioteca.model.Prestamo;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author camilaailen
 */
public class PrestamoJpaController implements Serializable {

    public PrestamoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public PrestamoJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prestamo prestamo) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Miembro unMiembro = prestamo.getUnMiembro();
            if (unMiembro != null) {
                unMiembro = em.getReference(unMiembro.getClass(), unMiembro.getId());
                prestamo.setUnMiembro(unMiembro);
            }
            Copia unCopia = prestamo.getUnCopia();
            if (unCopia != null) {
                unCopia = em.getReference(unCopia.getClass(), unCopia.getId());
                prestamo.setUnCopia(unCopia);
            }
            em.persist(prestamo);
            if (unMiembro != null) {
                unMiembro.getListaPrestamos().add(prestamo);
                unMiembro = em.merge(unMiembro);
            }
            if (unCopia != null) {
                unCopia.getListaPrestamos().add(prestamo);
                unCopia = em.merge(unCopia);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prestamo prestamo) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prestamo persistentPrestamo = em.find(Prestamo.class, prestamo.getId());
            Miembro unMiembroOld = persistentPrestamo.getUnMiembro();
            Miembro unMiembroNew = prestamo.getUnMiembro();
            Copia unCopiaOld = persistentPrestamo.getUnCopia();
            Copia unCopiaNew = prestamo.getUnCopia();
            if (unMiembroNew != null) {
                unMiembroNew = em.getReference(unMiembroNew.getClass(), unMiembroNew.getId());
                prestamo.setUnMiembro(unMiembroNew);
            }
            if (unCopiaNew != null) {
                unCopiaNew = em.getReference(unCopiaNew.getClass(), unCopiaNew.getId());
                prestamo.setUnCopia(unCopiaNew);
            }
            prestamo = em.merge(prestamo);
            if (unMiembroOld != null && !unMiembroOld.equals(unMiembroNew)) {
                unMiembroOld.getListaPrestamos().remove(prestamo);
                unMiembroOld = em.merge(unMiembroOld);
            }
            if (unMiembroNew != null && !unMiembroNew.equals(unMiembroOld)) {
                unMiembroNew.getListaPrestamos().add(prestamo);
                unMiembroNew = em.merge(unMiembroNew);
            }
            if (unCopiaOld != null && !unCopiaOld.equals(unCopiaNew)) {
                unCopiaOld.getListaPrestamos().remove(prestamo);
                unCopiaOld = em.merge(unCopiaOld);
            }
            if (unCopiaNew != null && !unCopiaNew.equals(unCopiaOld)) {
                unCopiaNew.getListaPrestamos().add(prestamo);
                unCopiaNew = em.merge(unCopiaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = prestamo.getId();
                if (findPrestamo(id) == null) {
                    throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.");
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
            Prestamo prestamo;
            try {
                prestamo = em.getReference(Prestamo.class, id);
                prestamo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.", enfe);
            }
            Miembro unMiembro = prestamo.getUnMiembro();
            if (unMiembro != null) {
                unMiembro.getListaPrestamos().remove(prestamo);
                unMiembro = em.merge(unMiembro);
            }
            Copia unCopia = prestamo.getUnCopia();
            if (unCopia != null) {
                unCopia.getListaPrestamos().remove(prestamo);
                unCopia = em.merge(unCopia);
            }
            em.remove(prestamo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Prestamo> findPrestamoEntities() {
        return findPrestamoEntities(true, -1, -1);
    }

    public List<Prestamo> findPrestamoEntities(int maxResults, int firstResult) {
        return findPrestamoEntities(false, maxResults, firstResult);
    }

    private List<Prestamo> findPrestamoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prestamo.class));
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

    public Prestamo findPrestamo(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Prestamo.class, id);
        } finally {
            em.close();
        }
    }

    public int getPrestamoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prestamo> rt = cq.from(Prestamo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
