package fi.metatavu.famifarm.rest.translate;

import fi.metatavu.famifarm.rest.model.PackagingFilmBatch;

import javax.enterprise.context.ApplicationScoped;

/**
 * Translator for packaging film batches
 *
 * @author Katja Danilova
 */
@ApplicationScoped
public class PackagingFilmBatchTranslator extends AbstractTranslator {

  /**
   * Translates JPA packaging film batch object into REST packaging film batch object
   *
   * @param entity JPA packaging film batch object
   * @return REST packaging film batch object
   */
  public PackagingFilmBatch translatePackagingFilmBatch(fi.metatavu.famifarm.persistence.model.PackagingFilmBatch entity) {
    if (entity == null) {
      return null;
    }

    PackagingFilmBatch result = new PackagingFilmBatch();
    result.setId(entity.getId());
    result.setName(entity.getName());
    result.setActive(entity.getActive());
    result.setTime(entity.getArrivalTime());
    return result;
  }
}
