package fi.metatavu.famifarm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StorageDiscardTranslator extends AbstractTranslator {

    /**
     * Translates JPA storage discard object into REST seed object
     *
     * @param storageDiscard JPA storage discard object
     * @return REST storage discard
     */
    public fi.metatavu.famifarm.rest.model.StorageDiscard translateStorageDiscard(fi.metatavu.famifarm.persistence.model.StorageDiscard storageDiscard) {
        if (storageDiscard == null) {
            return null;
        }

        fi.metatavu.famifarm.rest.model.StorageDiscard result = new fi.metatavu.famifarm.rest.model.StorageDiscard();
        result.setId(storageDiscard.getId());
        result.setDiscardAmount(storageDiscard.getDiscardAmount());
        result.setDiscardDate(storageDiscard.getDiscardDate());
        result.setPackageSizeId(storageDiscard.getPackageSize().getId());
        result.setProductId(storageDiscard.getProduct().getId());
        return result;
    }
}
