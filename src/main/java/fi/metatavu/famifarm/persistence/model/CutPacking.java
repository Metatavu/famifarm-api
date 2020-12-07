package fi.metatavu.famifarm.persistence.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class CutPacking {
    @Id
    @Column(nullable = false)
    @NotNull
    private UUID id;

    @ManyToOne(optional = false)
    private Product product;

    @ManyToOne(optional = false)
    private ProductionLine productionLine;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private OffsetDateTime sowingDay;

    @Column(nullable = false)
    private OffsetDateTime cuttingDay;

    @Column(nullable = false)
    private String producer;

    @Column(nullable = false)
    private String contactInformation;

    @Column(nullable = false)
    private int gutterCount;

    @Column(nullable = false)
    private int gutterHoleCount;

    @Column(nullable = false)
    private String storageCondition;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime modifiedAt;

    @Column(nullable = false)
    @NotNull
    private UUID creatorId;

    @Column(nullable = false)
    @NotNull
    private UUID lastModifierId;

    public String getStorageCondition() {
        return this.storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public int getGutterCount() {
        return this.gutterCount;
    }

    public void setGutterCount(int gutterCount) {
        this.gutterCount = gutterCount;
    }

    public int getGutterHoleCount() {
        return gutterHoleCount;
    }

    public void setGutterHoleCount(int gutterHoleCount) {
        this.gutterHoleCount = gutterHoleCount;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public OffsetDateTime getCuttingDay() {
        return this.cuttingDay;
    }

    public void setCuttingDay(OffsetDateTime cuttingDay) {
        this.cuttingDay = cuttingDay;
    }

    public OffsetDateTime getSowingDay() {
        return this.sowingDay;
    }

    public void setSowingDay(OffsetDateTime sowingDay) {
        this.sowingDay = sowingDay;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ProductionLine getProductionLine() {
        return this.productionLine;
    }

    public void setProductionLine(ProductionLine productionLine) {
        this.productionLine = productionLine;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(OffsetDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @PrePersist
    public void onCreate() {
        setCreatedAt(OffsetDateTime.now());
        setModifiedAt(OffsetDateTime.now());
    }
}

