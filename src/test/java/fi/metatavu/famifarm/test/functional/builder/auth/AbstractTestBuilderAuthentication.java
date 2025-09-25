package fi.metatavu.famifarm.test.functional.builder.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.test.functional.builder.impl.*;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTestBuilderAuthentication implements AutoCloseable {

  private ReportTestBuilderResource reports;
  private SeedTestBuilderResource seeds;
  private SeedBatchTestBuilderResource seedBatches;
  private WastageReasonTestBuilderResource wastageReasons;
  private ProductTestBuilderResource products;
  private PackageSizeTestBuilderResource packageSizes;
  private ProductionLineTestBuilderResource productionLines;
  private PerformedCultivationActionTestBuilderResource performedCultivationActions;
  private PestTestBuilderResource pests;
  private EventTestBuilderResource events;
  private DraftTestBuilderResource drafts;
  private PackingTestBuilderResource packings;
  private PrintingTestBuilderResource printers;
  private CampaignTestBuilderResource campaigns;
  private CutPackingTestBuilderResource cutPackings;
  private StorageDiscardTestBuilderResource storageDiscards;
  private PackagingFilmBatchTestResource packagingFilmBatches;
  private List<AutoCloseable> closables = new ArrayList<>();

  /**
   * Returns a test builder resource for campaigns
   *
   * @return test builder resource for campaigns
   * @throws IOException thrown when authentication fails
   */
  public CampaignTestBuilderResource campaigns() throws IOException {
    if (campaigns != null) {
      return campaigns;
    }

    return campaigns = this.addClosable(new CampaignTestBuilderResource(createClient()));
  }

  /**
   * Returns a test builder resource for printers
   *
   * @return test builder resource for printers
   * @throws IOException thrown when authentication fails
   */
  public PrintingTestBuilderResource printers() throws IOException {
    if (packings != null) {
      return printers;
    }

    return printers = new PrintingTestBuilderResource(createClient());
  }
  /**
   * Returns a test builder resource for packings
   *
   * @return test builder resource for packings
   * @throws IOException thrown when autherntication fails
   */
  public PackingTestBuilderResource packings() throws IOException {
    if (packings != null) {
      return packings;
    }
    
    return packings = this.addClosable(new PackingTestBuilderResource(createClient()));
  }
  /**
   * Returns test builder resource for reports
   * 
   * @return test builder resource for reports
   * @throws IOException thrown when authentication fails
   */
  public ReportTestBuilderResource reports() throws IOException {
    if (reports != null) {
      return reports;
    }
    
    return reports = this.addClosable(new ReportTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for seeds
   * 
   * @return test builder resource for seeds
   * @throws IOException thrown when authentication fails
   */
  public SeedTestBuilderResource seeds() throws IOException {
    if (seeds != null) {
      return seeds;
    }
    
    return seeds = this.addClosable(new SeedTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for wastage reasons
   * 
   * @return test builder resource for wastage reasons
   * @throws IOException thrown when authentication fails
   */
  public WastageReasonTestBuilderResource wastageReasons() throws IOException {
    if (wastageReasons != null) {
      return wastageReasons;
    }
    
    return wastageReasons = this.addClosable(new WastageReasonTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for seed batches
   * 
   * @return test builder resource for seed batches
   * @throws IOException thrown when authentication fails
   */
  public SeedBatchTestBuilderResource seedBatches() throws IOException {
    if (seedBatches != null) {
      return seedBatches;
    }
    
    return seedBatches = this.addClosable(new SeedBatchTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for products
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public ProductTestBuilderResource products() throws IOException {
    if (products != null) {
      return products;
    }
    
    return products = this.addClosable(new ProductTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for package sizes
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public PackageSizeTestBuilderResource packageSizes() throws IOException {
    if (packageSizes != null) {
      return packageSizes;
    }
    
    return packageSizes = this.addClosable(new PackageSizeTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for production lines
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public ProductionLineTestBuilderResource productionLines() throws IOException {
    if (productionLines != null) {
      return productionLines;
    }
    
    return productionLines = this.addClosable(new ProductionLineTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for performed cultivation actions
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public PestTestBuilderResource pests() throws IOException {
    if (pests != null) {
      return pests;
    }

    return pests = this.addClosable(new PestTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for performed cultivation actions
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public PerformedCultivationActionTestBuilderResource performedCultivationActions() throws IOException {
    if (performedCultivationActions != null) {
      return performedCultivationActions;
    }

    return performedCultivationActions = this.addClosable(new PerformedCultivationActionTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for events
   * 
   * @return test builder resource for events
   * @throws IOException thrown when authentication fails
   */
  public EventTestBuilderResource events() throws IOException {
    if (events != null) {
      return events;
    }
    
    return events = this.addClosable(new EventTestBuilderResource(createClient()));
  }
  
  /**
   * Returns test builder resource for drafts
   * 
   * @return test builder resource for drafts
   * @throws IOException thrown when authentication fails
   */
  public DraftTestBuilderResource drafts() throws IOException {
    if (drafts != null) {
      return drafts;
    }
    
    return drafts = this.addClosable(new DraftTestBuilderResource(createClient()));
  }

  /**
   * Returns a test builder resource for cut packings
   *
   * @return a test builder resource for cut packings
   * @throws IOException thrown when authentication fails
   */
  public CutPackingTestBuilderResource cutPackings() throws IOException {
    if (cutPackings != null) {
      return cutPackings;
    }

    return cutPackings = this.addClosable(new CutPackingTestBuilderResource(createClient()));
  }

  /**
   * Returns test builder resource for storage discards
   *
   * @return a test builder resource for storage discards
   * @throws IOException thrown when authentication fails
   */
  public StorageDiscardTestBuilderResource storageDiscards() throws IOException {
    if (storageDiscards != null) {
      return storageDiscards;
    }

    return storageDiscards = this.addClosable(new StorageDiscardTestBuilderResource(createClient()));
  }

  public PackagingFilmBatchTestResource packagingFilmBatches() throws IOException {
    if (packagingFilmBatches != null) {
      return packagingFilmBatches;
    }

    return packagingFilmBatches = this.addClosable(new PackagingFilmBatchTestResource(createClient()));
  }

  /**
   * Creates an API client
   * 
   * @return an API client
   * @throws IOException thrown when authentication fails
   */
  protected abstract ApiClient createClient() throws IOException;
  
  /**
   * Adds closable into clean queue
   * 
   * @param closable closeable
   * @return given instance
   */
  private <T extends AutoCloseable> T addClosable(T closable) {
    closables.add(closable);
    return closable;
  }

  @Override
  public void close() throws Exception {
    for (int i = closables.size() - 1; i >= 0; i--) {
      closables.get(i).close();
    }
    
    seeds = null;
  }

}
