package fi.metatavu.famifarm.rest.translate;

import fi.metatavu.famifarm.rest.model.CutPacking;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CutPackingTranslator extends AbstractTranslator  {
    /**
     * Translates a JPA cut packing into a REST cut packing
     *
     * @param entity a JPA entity to translated
     *
     * @return a translated REST entity
     */
    public CutPacking translate (fi.metatavu.famifarm.persistence.model.CutPacking entity) {
        CutPacking cutPacking = new CutPacking();
        cutPacking.setId(entity.getId());
        cutPacking.setContactInformation(entity.getContactInformation());
        cutPacking.setCuttingDay(entity.getCuttingDay());
        cutPacking.setGutterCount(entity.getGutterCount());
        cutPacking.setGutterHoleCount(entity.getGutterHoleCount());
        cutPacking.setProducer(entity.getProducer());
        cutPacking.setProductId(entity.getProduct().getId());
        cutPacking.setProductionLineId(entity.getProductionLine().getId());
        cutPacking.setSowingDay(entity.getSowingDay());
        cutPacking.setWeight(entity.getWeight());

        return cutPacking;
    }
}
