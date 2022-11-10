package fi.metatavu.famifarm.persistence.model;

import fi.metatavu.famifarm.rest.model.Facility;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA entity for discards from storage
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class StorageDiscard {

    @Id
    @Column(nullable = false)
    @NotNull
    private UUID id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private PackageSize packageSize;

    @Column(nullable = false)
    private OffsetDateTime discardDate;

    @Column(nullable = false)
    private Integer discardAmount;

    @Column(nullable = false)
    @NotNull
    private UUID creatorId;

    @Column(nullable = false)
    @NotNull
    private UUID lastModifierId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Facility facility;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PackageSize getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(PackageSize packageSize) {
        this.packageSize = packageSize;
    }

    public OffsetDateTime getDiscardDate() {
        return discardDate;
    }

    public void setDiscardDate(OffsetDateTime discardDate) {
        this.discardDate = discardDate;
    }

    public Integer getDiscardAmount() {
        return discardAmount;
    }

    public void setDiscardAmount(Integer discardAmount) {
        this.discardAmount = discardAmount;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public UUID getLastModifierId() {
        return lastModifierId;
    }

    public void setLastModifierId(UUID lastModifierId) {
        this.lastModifierId = lastModifierId;
    }

    public Facility getFacility()  { return facility; }

    public void setFacility(Facility facility) { this.facility = facility; }
}
