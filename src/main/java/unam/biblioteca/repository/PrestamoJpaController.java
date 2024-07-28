
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
            Miembro idMiembro = prestamo.getIdMiembro();
            if (idMiembro != null) {
                idMiembro = em.getReference(idMiembro.getClass(), idMiembro.getId());
                prestamo.setIdMiembro(idMiembro);
            }
            Copia idCopia = prestamo.getIdCopia();
            if (idCopia != null) {
                idCopia = em.getReference(idCopia.getClass(), idCopia.getId());
                prestamo.setIdCopia(idCopia);
            }
            em.persist(prestamo);
            if (idMiembro != null) {
                idMiembro.getListaPrestamos().add(prestamo);
                idMiembro = em.merge(idMiembro);
            }
            if (idCopia != null) {
                idCopia.getListaPrestamos().add(prestamo);
                idCopia = em.merge(idCopia);
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
            Miembro idMiembroOld = persistentPrestamo.getIdMiembro();
            Miembro idMiembroNew = prestamo.getIdMiembro();
            Copia idCopiaOld = persistentPrestamo.getIdCopia();
            Copia idCopiaNew = prestamo.getIdCopia();
            if (idMiembroNew != null) {
                idMiembroNew = em.getReference(idMiembroNew.getClass(), idMiembroNew.getId());
                prestamo.setIdMiembro(idMiembroNew);
            }
            if (idCopiaNew != null) {
                idCopiaNew = em.getReference(idCopiaNew.getClass(), idCopiaNew.getId());
                prestamo.setIdCopia(idCopiaNew);
            }
            prestamo = em.merge(prestamo);
            if (idMiembroOld != null && !idMiembroOld.equals(idMiembroNew)) {
                idMiembroOld.getListaPrestamos().remove(prestamo);
                idMiembroOld = em.merge(idMiembroOld);
            }
            if (idMiembroNew != null && !idMiembroNew.equals(idMiembroOld)) {
                idMiembroNew.getListaPrestamos().add(prestamo);
                idMiembroNew = em.merge(idMiembroNew);
            }
            if (idCopiaOld != null && !idCopiaOld.equals(idCopiaNew)) {
                idCopiaOld.getListaPrestamos().remove(prestamo);
                idCopiaOld = em.merge(idCopiaOld);
            }
            if (idCopiaNew != null && !idCopiaNew.equals(idCopiaOld)) {
                idCopiaNew.getListaPrestamos().add(prestamo);
                idCopiaNew = em.merge(idCopiaNew);
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
            Miembro idMiembro = prestamo.getIdMiembro();
            if (idMiembro != null) {
                idMiembro.getListaPrestamos().remove(prestamo);
                idMiembro = em.merge(idMiembro);
            }
            Copia idCopia = prestamo.getIdCopia();
            if (idCopia != null) {
                idCopia.getListaPrestamos().remove(prestamo);
                idCopia = em.merge(idCopia);
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
