package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.famifarm.rest.model.PackageSize;

/**
 * Translator for package size
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class PackageSizeTranslator extends AbstractTranslator {
  
  /**
   * Translates JPA package size object into REST seed object
   * 
   * @param seed JPA seed object
   * @return REST seed
   */
  public PackageSize translatePackageSize(fi.metatavu.famifarm.persistence.model.PackageSize packageSize) {
    if (packageSize == null) {
      return null;
    }
    
    PackageSize result = new PackageSize();
    result.setId(packageSize.getId());
    result.setName(packageSize.getName());

    return result;
  }
  
}
