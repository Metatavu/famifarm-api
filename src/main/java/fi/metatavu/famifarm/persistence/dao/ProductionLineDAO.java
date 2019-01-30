package fi.metatavu.famifarm.persistence.dao;

import java.util.UUID;

import fi.metatavu.famifarm.persistence.model.ProductionLine;

/**
 * DAO class for seed batches
 * 
 * @author Ville Koivukangas
 */
public class ProductionLineDAO extends AbstractDAO<ProductionLine> {

  /**
   * Creates new seed production line
   *
   * @param id id
   * @param lineNumber lineNumber
   * @param creatorId creatorId
   * @param lastModifierId lastModifierId
   * @return created production line
   */
  public ProductionLine create(UUID id, int lineNumber, UUID creatorId, UUID lastModifierId) {
    ProductionLine productionLine = new ProductionLine();
    productionLine.setId(id);
    productionLine.setLineNumber(lineNumber);
    productionLine.setCreatorId(creatorId);
    productionLine.setLastModifierId(lastModifierId);
    return persist(productionLine);
  }

  /**
   * Updates line number
   *
   * @param productionLine productionLine
   * @param lineNumber lineNumber
   * @param lastModifier modifier
   * @return updated production line
   */
  public ProductionLine updateLineNumber(ProductionLine productionLine, int lineNumber, UUID lastModifierId) {
    productionLine.setLastModifierId(lastModifierId);
    productionLine.setLineNumber(lineNumber);
    return persist(productionLine);
  }
}
