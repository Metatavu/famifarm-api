package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.famifarm.persistence.model.Draft;
import fi.metatavu.famifarm.persistence.model.Draft_;

/**
 * DAO class for drafts
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class DraftDAO extends AbstractDAO<Draft> {

  /**
   * Creates new draft
   *
   * @param id id
   * @param type type
   * @param data data
   * @param creatorId creator id
   * @param lastModifierId last modifier
   * @return created draft
   */
  public Draft create(UUID id, String type, String data, UUID creatorId, UUID lastModifierId) {
    Draft draft = new Draft();
    draft.setType(type);
    draft.setData(data);
    draft.setId(id);
    draft.setCreatorId(creatorId);
    draft.setLastModifierId(lastModifierId);
    return persist(draft);
  }
  
  /**
   * Finds a draft by creator and type
   * 
   * @param creatorId creatorId
   * @param type type
   * @return found draft or null if not found
   */
  public Draft findByCreatorIdAndType(UUID creatorId, String type) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Draft> criteria = criteriaBuilder.createQuery(Draft.class);
    Root<Draft> root = criteria.from(Draft.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.and(
      criteriaBuilder.equal(root.get(Draft_.type), type),
      criteriaBuilder.equal(root.get(Draft_.creatorId), creatorId)
    ));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }  

  /**
   * Updates type
   *
   * @param type type
   * @param lastModifierId last modifier
   * @return updated draft
   */
  public Draft updateType(Draft draft, String type, UUID lastModifierId) {
    draft.setLastModifierId(lastModifierId);
    draft.setType(type);
    return persist(draft);
  }

  /**
   * Updates data
   *
   * @param data data
   * @param lastModifierId last modifier
   * @return updated draft
   */
  public Draft updateData(Draft draft, String data, UUID lastModifierId) {
    draft.setLastModifierId(lastModifierId);
    draft.setData(data);
    return persist(draft);
  }

}
