package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.PackingBasket;
import fi.metatavu.famifarm.persistence.model.PackingBasket_;
import fi.metatavu.famifarm.persistence.model.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for PackingBasket
 *
 * @see AbstractDAO
 * @see PackingBasket
 * @see ApplicationScoped
 */
@ApplicationScoped
public class PackingBasketDAO extends AbstractDAO<PackingBasket> {

  /**
   * Creates new PackingBasket
   *
   * @param packing packing
   * @param product product
   * @param count   count
   * @return created PackingBasket
   */
  public PackingBasket create(
    UUID id,
    Packing packing,
    Product product,
    Integer count
  ) {
    PackingBasket packingBasket = new PackingBasket();
    packingBasket.setId(id);
    packingBasket.setPacking(packing);
    packingBasket.setProduct(product);
    packingBasket.setCount(count);
    return persist(packingBasket);
  }

  /**
   * Lists packing baskets by packing
   *
   * @param packing packing
   * @return packing baskets
   */
  public List<PackingBasket> listByPacking(Packing packing) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PackingBasket> criteria = criteriaBuilder.createQuery(PackingBasket.class);
    Root<PackingBasket> root = criteria.from(PackingBasket.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PackingBasket_.packing), packing));

    TypedQuery<PackingBasket> query = entityManager.createQuery(criteria);
    return query.getResultList();
  }

  /**
   * Lists packing backets by product
   *
   * @param product product filter
   * @return packing baskets
   */
  public List<PackingBasket> listByProduct(Product product) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PackingBasket> criteria = criteriaBuilder.createQuery(PackingBasket.class);
    Root<PackingBasket> root = criteria.from(PackingBasket.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PackingBasket_.product), product));

    TypedQuery<PackingBasket> query = entityManager.createQuery(criteria);
    return query.getResultList();
  }
}
