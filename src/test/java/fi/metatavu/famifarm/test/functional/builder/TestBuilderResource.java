package fi.metatavu.famifarm.test.functional.builder;

import java.util.List;

/**
 * Interface describing a test builder resource.
 * 
 * @author Antti Leppä
 *
 * @param <T> resource type
 */
public interface TestBuilderResource <T> extends AutoCloseable {
  
  /**
   * Adds closable into clean queue
   * 
   * @param t closeable
   * @return given instance
   */
  public T addClosable(T t);
  
  /**
   * Adds list of closables into clean queue
   * 
   * @param t closeable
   * @return given instance
   */
  public List<T> addClosables(List<T> t);
  
  /**
   * Cleans given resource
   * 
   * @param t resource
   */
  public void clean(T t);

}
