package fi.metatavu.famifarm.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.famifarm.events.SowingEventController;
import fi.metatavu.famifarm.persistence.model.ProductionLine;
import fi.metatavu.famifarm.persistence.model.SowingEvent;
import fi.metatavu.famifarm.rest.model.Batch;

/**
 * Translator for batches
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class BatchTranslator extends AbstractTranslator {
  
  @Inject
  private SowingEventController sowingEventController;
  
  /**
   * Translates JPA batch object into REST batch object
   * 
   * @param seed JPA seed object
   * @return REST seed
   */
  public Batch translateBatch(fi.metatavu.famifarm.persistence.model.Batch batch) {
    if (batch == null) {
      return null;
    }
    
    Batch result = new Batch();
    result.setId(batch.getId());
    result.setCreatedAt(batch.getCreatedAt());
    result.setPhase(batch.getPhase());

    if (batch.getProduct() != null) {
      result.setProductId(batch.getProduct().getId());
    }
    
    List<String> sowingLineNumbers = sowingEventController.listBatchSowingEvents(batch).stream()
      .map(SowingEvent::getProductionLine)
      .map(ProductionLine::getLineNumber)
      .distinct()
      .collect(Collectors.toList());
    
    result.setSowingLineNumbers(sowingLineNumbers);

    return result;
  }
  
}
