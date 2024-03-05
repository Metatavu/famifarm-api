package fi.metatavu.famifarm.persistence.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Baskets which are used in harvest events
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class HarvestBasket {

    @Id
    @Column(nullable = false)
    @NotNull
    private UUID id;

    @Column(nullable = false)
    @NotNull
    private Float weight;

    @ManyToOne(optional = false)
    private HarvestEvent harvestEvent;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public HarvestEvent getHarvestEvent() {
        return harvestEvent;
    }

    public void setHarvestEvent(HarvestEvent harvestEvent) {
        this.harvestEvent = harvestEvent;
    }
}
