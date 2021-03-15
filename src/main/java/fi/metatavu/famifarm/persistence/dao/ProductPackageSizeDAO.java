package fi.metatavu.famifarm.persistence.dao;

import fi.metatavu.famifarm.persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for product package size
 *
 * @author Katja Danilova
 */
@ApplicationScoped
public class ProductPackageSizeDAO extends AbstractDAO<ProductPackageSize> {

    /**
     * Creates new ProductPackageSize
     *
     * @param id id
     * @param product product
     * @param packageSize packageSize
     * @return created product package size
     */
    public ProductPackageSize create(UUID id, Product product, PackageSize packageSize) {
        ProductPackageSize productPackageSize = new ProductPackageSize();
        productPackageSize.setId(id);
        productPackageSize.setProduct(product);
        productPackageSize.setPackageSize(packageSize);
        return persist(productPackageSize);
    }

    /**
     * Lists by package size
     *
     * @param packageSize packageSize
     * @return list of product package size entries
     */
    public List<ProductPackageSize> listByPackageSize(PackageSize packageSize) {
        EntityManager entityManager = getEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductPackageSize> criteria = criteriaBuilder.createQuery(ProductPackageSize.class);
        Root<ProductPackageSize> root = criteria.from(ProductPackageSize.class);
        criteria.select(root);
        criteria.where(criteriaBuilder.equal(root.get(ProductPackageSize_.packageSize), packageSize));
        return entityManager.createQuery(criteria).getResultList();
    }

    /**
     * Lists by product
     *
     * @param product product
     * @return list of product package size entries
     */
    public List<ProductPackageSize> listByProduct(Product product) {
        EntityManager entityManager = getEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductPackageSize> criteria = criteriaBuilder.createQuery(ProductPackageSize.class);
        Root<ProductPackageSize> root = criteria.from(ProductPackageSize.class);
        criteria.select(root);
        criteria.where(criteriaBuilder.equal(root.get(ProductPackageSize_.product), product));
        return entityManager.createQuery(criteria).getResultList();
    }

    /**
     * Updates product package size
     *
     * @param productPackageSize productPackageSize
     * @param packageSize new packageSize
     * @return updated productPackageSize
     */
    public ProductPackageSize updatePackageSize(ProductPackageSize productPackageSize, PackageSize packageSize) {
        productPackageSize.setPackageSize(packageSize);
        return persist(productPackageSize);
    }
}
