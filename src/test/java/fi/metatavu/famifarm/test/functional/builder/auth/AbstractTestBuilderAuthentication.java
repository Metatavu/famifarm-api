package fi.metatavu.famifarm.test.functional.builder.auth;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import fi.metatavu.famifarm.ApiClient;
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
  
  private SeedTestBuilderResource seeds;
  private TeamTestBuilderResource teams;
  private SeedBatchTestBuilderResource seedBatches;
  private WastageReasonTestBuilderResource wastageReasons;
  private List<AutoCloseable> closables = new ArrayList<>();
  
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
