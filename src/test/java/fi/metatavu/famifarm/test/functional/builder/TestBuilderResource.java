package fi.metatavu.famifarm.test.functional.builder;

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
   * Cleans given resource
   * 
   * @param t resource
   */
  public void clean(T t);

}
