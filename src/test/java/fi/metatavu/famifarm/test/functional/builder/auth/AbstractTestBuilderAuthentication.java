package fi.metatavu.famifarm.test.functional.builder.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.test.functional.builder.impl.BatchTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.DraftTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.EventTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.PackageSizeTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.PerformedCultivationActionTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.ProductTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.ProductionLineTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.ReportTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.SeedBatchTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.SeedTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.TeamTestBuilderResource;
import fi.metatavu.famifarm.test.functional.builder.impl.WastageReasonTestBuilderResource;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTestBuilderAuthentication implements AutoCloseable {

  private ReportTestBuilderResource reports;
  private SeedTestBuilderResource seeds;
  private TeamTestBuilderResource teams;
  private SeedBatchTestBuilderResource seedBatches;
  private WastageReasonTestBuilderResource wastageReasons;
  private ProductTestBuilderResource products;
  private PackageSizeTestBuilderResource packageSizes;
  private BatchTestBuilderResource batches;
  private ProductionLineTestBuilderResource productionLines;
  private PerformedCultivationActionTestBuilderResource performedCultivationActions;
  private EventTestBuilderResource events;
  private DraftTestBuilderResource drafts;
  private List<AutoCloseable> closables = new ArrayList<>();
  
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
   * Returns test builder resource for teams
   * 
   * @return test builder resource for teams
   * @throws IOException thrown when authentication fails
   */
  public TeamTestBuilderResource teams() throws IOException {
    if (teams != null) {
      return teams;
    }
    
    return teams = this.addClosable(new TeamTestBuilderResource(createClient()));
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
   * Returns test builder resource for batches
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public BatchTestBuilderResource batches() throws IOException {
    if (batches != null) {
      return batches;
    }
    
    return batches = this.addClosable(new BatchTestBuilderResource(createClient()));
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
