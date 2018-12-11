package fi.metatavu.famifarm.test.functional.builder.auth;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.test.functional.builder.impl.SeedTestBuilderResource;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTestBuilderAuthentication implements AutoCloseable {
  
  private List<AutoCloseable> closables = new ArrayList<>();
  
  /**
   * Returns test builder resource for seeds
   * 
   * @return test builder resource for seeds
   * @throws IOException thrown when authentication fails
   */
  public SeedTestBuilderResource seeds() throws IOException {
    return this.addClosable(new SeedTestBuilderResource(createClient()));
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
  }

}
