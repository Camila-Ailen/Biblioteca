
package unam.biblioteca.repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unam.biblioteca.model.Miembro;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unam.biblioteca.model.Rol;
import unam.biblioteca.repository.exceptions.NonexistentEntityException;


public class RolJpaController implements Serializable {

    public RolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public RolJpaController () {
        emf = Persistence.createEntityManagerFactory("bibliotecaPU");
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rol rol) {
        if (rol.getListaMiembros() == null) {
            rol.setListaMiembros(new ArrayList<Miembro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArrayList<Miembro> attachedListaMiembros = new ArrayList<Miembro>();
            for (Miembro listaMiembrosMiembroToAttach : rol.getListaMiembros()) {
                listaMiembrosMiembroToAttach = em.getReference(listaMiembrosMiembroToAttach.getClass(), listaMiembrosMiembroToAttach.getId());
                attachedListaMiembros.add(listaMiembrosMiembroToAttach);
            }
            rol.setListaMiembros(attachedListaMiembros);
            em.persist(rol);
            for (Miembro listaMiembrosMiembro : rol.getListaMiembros()) {
                Rol oldIdRolOfListaMiembrosMiembro = listaMiembrosMiembro.getIdRol();
                listaMiembrosMiembro.setIdRol(rol);
                listaMiembrosMiembro = em.merge(listaMiembrosMiembro);
                if (oldIdRolOfListaMiembrosMiembro != null) {
                    oldIdRolOfListaMiembrosMiembro.getListaMiembros().remove(listaMiembrosMiembro);
                    oldIdRolOfListaMiembrosMiembro = em.merge(oldIdRolOfListaMiembrosMiembro);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rol rol) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol persistentRol = em.find(Rol.class, rol.getId());
            ArrayList<Miembro> listaMiembrosOld = persistentRol.getListaMiembros();
            ArrayList<Miembro> listaMiembrosNew = rol.getListaMiembros();
            ArrayList<Miembro> attachedListaMiembrosNew = new ArrayList<Miembro>();
            for (Miembro listaMiembrosNewMiembroToAttach : listaMiembrosNew) {
                listaMiembrosNewMiembroToAttach = em.getReference(listaMiembrosNewMiembroToAttach.getClass(), listaMiembrosNewMiembroToAttach.getId());
                attachedListaMiembrosNew.add(listaMiembrosNewMiembroToAttach);
            }
            listaMiembrosNew = attachedListaMiembrosNew;
            rol.setListaMiembros(listaMiembrosNew);
            rol = em.merge(rol);
            for (Miembro listaMiembrosOldMiembro : listaMiembrosOld) {
                if (!listaMiembrosNew.contains(listaMiembrosOldMiembro)) {
                    listaMiembrosOldMiembro.setIdRol(null);
                    listaMiembrosOldMiembro = em.merge(listaMiembrosOldMiembro);
                }
            }
            for (Miembro listaMiembrosNewMiembro : listaMiembrosNew) {
                if (!listaMiembrosOld.contains(listaMiembrosNewMiembro)) {
                    Rol oldIdRolOfListaMiembrosNewMiembro = listaMiembrosNewMiembro.getIdRol();
                    listaMiembrosNewMiembro.setIdRol(rol);
                    listaMiembrosNewMiembro = em.merge(listaMiembrosNewMiembro);
                    if (oldIdRolOfListaMiembrosNewMiembro != null && !oldIdRolOfListaMiembrosNewMiembro.equals(rol)) {
                        oldIdRolOfListaMiembrosNewMiembro.getListaMiembros().remove(listaMiembrosNewMiembro);
                        oldIdRolOfListaMiembrosNewMiembro = em.merge(oldIdRolOfListaMiembrosNewMiembro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = rol.getId();
                if (findRol(id) == null) {
                    throw new NonexistentEntityException("The rol with id " + id + " no longer exists.");
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
            Rol rol;
            try {
                rol = em.getReference(Rol.class, id);
                rol.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rol with id " + id + " no longer exists.", enfe);
            }
            ArrayList<Miembro> listaMiembros = rol.getListaMiembros();
            for (Miembro listaMiembrosMiembro : listaMiembros) {
                listaMiembrosMiembro.setIdRol(null);
                listaMiembrosMiembro = em.merge(listaMiembrosMiembro);
            }
            em.remove(rol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rol> findRolEntities() {
        return findRolEntities(true, -1, -1);
    }

    public List<Rol> findRolEntities(int maxResults, int firstResult) {
        return findRolEntities(false, maxResults, firstResult);
    }

    private List<Rol> findRolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rol.class));
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

    public Rol findRol(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rol> rt = cq.from(Rol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
