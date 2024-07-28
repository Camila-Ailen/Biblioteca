
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
            Rack idRack = copia.getIdRack();
            if (idRack != null) {
                idRack = em.getReference(idRack.getClass(), idRack.getId());
                copia.setIdRack(idRack);
            }
            Libro idLibro = copia.getIdLibro();
            if (idLibro != null) {
                idLibro = em.getReference(idLibro.getClass(), idLibro.getId());
                copia.setIdLibro(idLibro);
            }
            ArrayList<Prestamo> attachedListaPrestamos = new ArrayList<Prestamo>();
            for (Prestamo listaPrestamosPrestamoToAttach : copia.getListaPrestamos()) {
                listaPrestamosPrestamoToAttach = em.getReference(listaPrestamosPrestamoToAttach.getClass(), listaPrestamosPrestamoToAttach.getId());
                attachedListaPrestamos.add(listaPrestamosPrestamoToAttach);
            }
            copia.setListaPrestamos(attachedListaPrestamos);
            em.persist(copia);
            if (idRack != null) {
                idRack.getListaCopias().add(copia);
                idRack = em.merge(idRack);
            }
            if (idLibro != null) {
                idLibro.getListaCopias().add(copia);
                idLibro = em.merge(idLibro);
            }
            for (Prestamo listaPrestamosPrestamo : copia.getListaPrestamos()) {
                Copia oldIdCopiaOfListaPrestamosPrestamo = listaPrestamosPrestamo.getIdCopia();
                listaPrestamosPrestamo.setIdCopia(copia);
                listaPrestamosPrestamo = em.merge(listaPrestamosPrestamo);
                if (oldIdCopiaOfListaPrestamosPrestamo != null) {
                    oldIdCopiaOfListaPrestamosPrestamo.getListaPrestamos().remove(listaPrestamosPrestamo);
                    oldIdCopiaOfListaPrestamosPrestamo = em.merge(oldIdCopiaOfListaPrestamosPrestamo);
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
            Rack idRackOld = persistentCopia.getIdRack();
            Rack idRackNew = copia.getIdRack();
            Libro idLibroOld = persistentCopia.getIdLibro();
            Libro idLibroNew = copia.getIdLibro();
            ArrayList<Prestamo> listaPrestamosOld = persistentCopia.getListaPrestamos();
            ArrayList<Prestamo> listaPrestamosNew = copia.getListaPrestamos();
            if (idRackNew != null) {
                idRackNew = em.getReference(idRackNew.getClass(), idRackNew.getId());
                copia.setIdRack(idRackNew);
            }
            if (idLibroNew != null) {
                idLibroNew = em.getReference(idLibroNew.getClass(), idLibroNew.getId());
                copia.setIdLibro(idLibroNew);
            }
            ArrayList<Prestamo> attachedListaPrestamosNew = new ArrayList<Prestamo>();
            for (Prestamo listaPrestamosNewPrestamoToAttach : listaPrestamosNew) {
                listaPrestamosNewPrestamoToAttach = em.getReference(listaPrestamosNewPrestamoToAttach.getClass(), listaPrestamosNewPrestamoToAttach.getId());
                attachedListaPrestamosNew.add(listaPrestamosNewPrestamoToAttach);
            }
            listaPrestamosNew = attachedListaPrestamosNew;
            copia.setListaPrestamos(listaPrestamosNew);
            copia = em.merge(copia);
            if (idRackOld != null && !idRackOld.equals(idRackNew)) {
                idRackOld.getListaCopias().remove(copia);
                idRackOld = em.merge(idRackOld);
            }
            if (idRackNew != null && !idRackNew.equals(idRackOld)) {
                idRackNew.getListaCopias().add(copia);
                idRackNew = em.merge(idRackNew);
            }
            if (idLibroOld != null && !idLibroOld.equals(idLibroNew)) {
                idLibroOld.getListaCopias().remove(copia);
                idLibroOld = em.merge(idLibroOld);
            }
            if (idLibroNew != null && !idLibroNew.equals(idLibroOld)) {
                idLibroNew.getListaCopias().add(copia);
                idLibroNew = em.merge(idLibroNew);
            }
            for (Prestamo listaPrestamosOldPrestamo : listaPrestamosOld) {
                if (!listaPrestamosNew.contains(listaPrestamosOldPrestamo)) {
                    listaPrestamosOldPrestamo.setIdCopia(null);
                    listaPrestamosOldPrestamo = em.merge(listaPrestamosOldPrestamo);
                }
            }
            for (Prestamo listaPrestamosNewPrestamo : listaPrestamosNew) {
                if (!listaPrestamosOld.contains(listaPrestamosNewPrestamo)) {
                    Copia oldIdCopiaOfListaPrestamosNewPrestamo = listaPrestamosNewPrestamo.getIdCopia();
                    listaPrestamosNewPrestamo.setIdCopia(copia);
                    listaPrestamosNewPrestamo = em.merge(listaPrestamosNewPrestamo);
                    if (oldIdCopiaOfListaPrestamosNewPrestamo != null && !oldIdCopiaOfListaPrestamosNewPrestamo.equals(copia)) {
                        oldIdCopiaOfListaPrestamosNewPrestamo.getListaPrestamos().remove(listaPrestamosNewPrestamo);
                        oldIdCopiaOfListaPrestamosNewPrestamo = em.merge(oldIdCopiaOfListaPrestamosNewPrestamo);
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
            Rack idRack = copia.getIdRack();
            if (idRack != null) {
                idRack.getListaCopias().remove(copia);
                idRack = em.merge(idRack);
            }
            Libro idLibro = copia.getIdLibro();
            if (idLibro != null) {
                idLibro.getListaCopias().remove(copia);
                idLibro = em.merge(idLibro);
            }
            ArrayList<Prestamo> listaPrestamos = copia.getListaPrestamos();
            for (Prestamo listaPrestamosPrestamo : listaPrestamos) {
                listaPrestamosPrestamo.setIdCopia(null);
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
