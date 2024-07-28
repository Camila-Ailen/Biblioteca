
package unam.biblioteca.repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unam.biblioteca.model.Rol;
import unam.biblioteca.model.Prestamo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unam.biblioteca.model.Miembro;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;


public class MiembroJpaController implements Serializable {

    public MiembroJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public MiembroJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Miembro miembro) {
        if (miembro.getListaPrestamos() == null) {
            miembro.setListaPrestamos(new ArrayList<Prestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol idRol = miembro.getIdRol();
            if (idRol != null) {
                idRol = em.getReference(idRol.getClass(), idRol.getId());
                miembro.setIdRol(idRol);
            }
            ArrayList<Prestamo> attachedListaPrestamos = new ArrayList<Prestamo>();
            for (Prestamo listaPrestamosPrestamoToAttach : miembro.getListaPrestamos()) {
                listaPrestamosPrestamoToAttach = em.getReference(listaPrestamosPrestamoToAttach.getClass(), listaPrestamosPrestamoToAttach.getId());
                attachedListaPrestamos.add(listaPrestamosPrestamoToAttach);
            }
            miembro.setListaPrestamos(attachedListaPrestamos);
            em.persist(miembro);
            if (idRol != null) {
                idRol.getListaMiembros().add(miembro);
                idRol = em.merge(idRol);
            }
            for (Prestamo listaPrestamosPrestamo : miembro.getListaPrestamos()) {
                Miembro oldIdMiembroOfListaPrestamosPrestamo = listaPrestamosPrestamo.getIdMiembro();
                listaPrestamosPrestamo.setIdMiembro(miembro);
                listaPrestamosPrestamo = em.merge(listaPrestamosPrestamo);
                if (oldIdMiembroOfListaPrestamosPrestamo != null) {
                    oldIdMiembroOfListaPrestamosPrestamo.getListaPrestamos().remove(listaPrestamosPrestamo);
                    oldIdMiembroOfListaPrestamosPrestamo = em.merge(oldIdMiembroOfListaPrestamosPrestamo);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Miembro miembro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Miembro persistentMiembro = em.find(Miembro.class, miembro.getId());
            Rol idRolOld = persistentMiembro.getIdRol();
            Rol idRolNew = miembro.getIdRol();
            ArrayList<Prestamo> listaPrestamosOld = persistentMiembro.getListaPrestamos();
            ArrayList<Prestamo> listaPrestamosNew = miembro.getListaPrestamos();
            if (idRolNew != null) {
                idRolNew = em.getReference(idRolNew.getClass(), idRolNew.getId());
                miembro.setIdRol(idRolNew);
            }
            ArrayList<Prestamo> attachedListaPrestamosNew = new ArrayList<Prestamo>();
            for (Prestamo listaPrestamosNewPrestamoToAttach : listaPrestamosNew) {
                listaPrestamosNewPrestamoToAttach = em.getReference(listaPrestamosNewPrestamoToAttach.getClass(), listaPrestamosNewPrestamoToAttach.getId());
                attachedListaPrestamosNew.add(listaPrestamosNewPrestamoToAttach);
            }
            listaPrestamosNew = attachedListaPrestamosNew;
            miembro.setListaPrestamos(listaPrestamosNew);
            miembro = em.merge(miembro);
            if (idRolOld != null && !idRolOld.equals(idRolNew)) {
                idRolOld.getListaMiembros().remove(miembro);
                idRolOld = em.merge(idRolOld);
            }
            if (idRolNew != null && !idRolNew.equals(idRolOld)) {
                idRolNew.getListaMiembros().add(miembro);
                idRolNew = em.merge(idRolNew);
            }
            for (Prestamo listaPrestamosOldPrestamo : listaPrestamosOld) {
                if (!listaPrestamosNew.contains(listaPrestamosOldPrestamo)) {
                    listaPrestamosOldPrestamo.setIdMiembro(null);
                    listaPrestamosOldPrestamo = em.merge(listaPrestamosOldPrestamo);
                }
            }
            for (Prestamo listaPrestamosNewPrestamo : listaPrestamosNew) {
                if (!listaPrestamosOld.contains(listaPrestamosNewPrestamo)) {
                    Miembro oldIdMiembroOfListaPrestamosNewPrestamo = listaPrestamosNewPrestamo.getIdMiembro();
                    listaPrestamosNewPrestamo.setIdMiembro(miembro);
                    listaPrestamosNewPrestamo = em.merge(listaPrestamosNewPrestamo);
                    if (oldIdMiembroOfListaPrestamosNewPrestamo != null && !oldIdMiembroOfListaPrestamosNewPrestamo.equals(miembro)) {
                        oldIdMiembroOfListaPrestamosNewPrestamo.getListaPrestamos().remove(listaPrestamosNewPrestamo);
                        oldIdMiembroOfListaPrestamosNewPrestamo = em.merge(oldIdMiembroOfListaPrestamosNewPrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = miembro.getId();
                if (findMiembro(id) == null) {
                    throw new NonexistentEntityException("The miembro with id " + id + " no longer exists.");
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
            Miembro miembro;
            try {
                miembro = em.getReference(Miembro.class, id);
                miembro.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The miembro with id " + id + " no longer exists.", enfe);
            }
            Rol idRol = miembro.getIdRol();
            if (idRol != null) {
                idRol.getListaMiembros().remove(miembro);
                idRol = em.merge(idRol);
            }
            ArrayList<Prestamo> listaPrestamos = miembro.getListaPrestamos();
            for (Prestamo listaPrestamosPrestamo : listaPrestamos) {
                listaPrestamosPrestamo.setIdMiembro(null);
                listaPrestamosPrestamo = em.merge(listaPrestamosPrestamo);
            }
            em.remove(miembro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Miembro> findMiembroEntities() {
        return findMiembroEntities(true, -1, -1);
    }

    public List<Miembro> findMiembroEntities(int maxResults, int firstResult) {
        return findMiembroEntities(false, maxResults, firstResult);
    }

    private List<Miembro> findMiembroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Miembro.class));
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

    public Miembro findMiembro(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Miembro.class, id);
        } finally {
            em.close();
        }
    }

    public int getMiembroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Miembro> rt = cq.from(Miembro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
