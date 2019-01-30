package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.Product;
import fi.metatavu.famifarm.rest.model.WastageReason;

/**
 * Translator for products
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class ProductsTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA product object into REST product object
   * 
   * @param product JPA product object
   * @return REST WastageReason
   */
  public Product translateProduct(fi.metatavu.famifarm.persistence.model.Product product) {
    if (product == null) {
      return null;
    }
    
    Product result = new Product();
    result.setId(product.getId());
    result.setName(translatelocalizedValue(product.getName()));

    return result;
  }
  
}