
package unam.biblioteca.repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unam.biblioteca.model.Copia;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unam.biblioteca.model.Rack;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;


public class RackJpaController implements Serializable {

    public RackJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public RackJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rack rack) {
        if (rack.getListaCopias() == null) {
            rack.setListaCopias(new ArrayList<Copia>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArrayList<Copia> attachedListaCopias = new ArrayList<Copia>();
            for (Copia listaCopiasCopiaToAttach : rack.getListaCopias()) {
                listaCopiasCopiaToAttach = em.getReference(listaCopiasCopiaToAttach.getClass(), listaCopiasCopiaToAttach.getId());
                attachedListaCopias.add(listaCopiasCopiaToAttach);
            }
            rack.setListaCopias(attachedListaCopias);
            em.persist(rack);
            for (Copia listaCopiasCopia : rack.getListaCopias()) {
                Rack oldIdRackOfListaCopiasCopia = listaCopiasCopia.getIdRack();
                listaCopiasCopia.setIdRack(rack);
                listaCopiasCopia = em.merge(listaCopiasCopia);
                if (oldIdRackOfListaCopiasCopia != null) {
                    oldIdRackOfListaCopiasCopia.getListaCopias().remove(listaCopiasCopia);
                    oldIdRackOfListaCopiasCopia = em.merge(oldIdRackOfListaCopiasCopia);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rack rack) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rack persistentRack = em.find(Rack.class, rack.getId());
            ArrayList<Copia> listaCopiasOld = persistentRack.getListaCopias();
            ArrayList<Copia> listaCopiasNew = rack.getListaCopias();
            ArrayList<Copia> attachedListaCopiasNew = new ArrayList<Copia>();
            for (Copia listaCopiasNewCopiaToAttach : listaCopiasNew) {
                listaCopiasNewCopiaToAttach = em.getReference(listaCopiasNewCopiaToAttach.getClass(), listaCopiasNewCopiaToAttach.getId());
                attachedListaCopiasNew.add(listaCopiasNewCopiaToAttach);
            }
            listaCopiasNew = attachedListaCopiasNew;
            rack.setListaCopias(listaCopiasNew);
            rack = em.merge(rack);
            for (Copia listaCopiasOldCopia : listaCopiasOld) {
                if (!listaCopiasNew.contains(listaCopiasOldCopia)) {
                    listaCopiasOldCopia.setIdRack(null);
                    listaCopiasOldCopia = em.merge(listaCopiasOldCopia);
                }
            }
            for (Copia listaCopiasNewCopia : listaCopiasNew) {
                if (!listaCopiasOld.contains(listaCopiasNewCopia)) {
                    Rack oldIdRackOfListaCopiasNewCopia = listaCopiasNewCopia.getIdRack();
                    listaCopiasNewCopia.setIdRack(rack);
                    listaCopiasNewCopia = em.merge(listaCopiasNewCopia);
                    if (oldIdRackOfListaCopiasNewCopia != null && !oldIdRackOfListaCopiasNewCopia.equals(rack)) {
                        oldIdRackOfListaCopiasNewCopia.getListaCopias().remove(listaCopiasNewCopia);
                        oldIdRackOfListaCopiasNewCopia = em.merge(oldIdRackOfListaCopiasNewCopia);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = rack.getId();
                if (findRack(id) == null) {
                    throw new NonexistentEntityException("The rack with id " + id + " no longer exists.");
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
            Rack rack;
            try {
                rack = em.getReference(Rack.class, id);
                rack.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rack with id " + id + " no longer exists.", enfe);
            }
            ArrayList<Copia> listaCopias = rack.getListaCopias();
            for (Copia listaCopiasCopia : listaCopias) {
                listaCopiasCopia.setIdRack(null);
                listaCopiasCopia = em.merge(listaCopiasCopia);
            }
            em.remove(rack);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rack> findRackEntities() {
        return findRackEntities(true, -1, -1);
    }

    public List<Rack> findRackEntities(int maxResults, int firstResult) {
        return findRackEntities(false, maxResults, firstResult);
    }

    private List<Rack> findRackEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rack.class));
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

    public Rack findRack(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rack.class, id);
        } finally {
            em.close();
        }
    }

    public int getRackCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rack> rt = cq.from(Rack.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
