package fi.metatavu.famifarm.test.functional.builder;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.ApiClient.Api;

/**
 * Abstract base class for test builder resources
 * 
 * @author Antti Leppä
 *
 * @param <T> type of resource
 * @param <A> type of API
 */
public abstract class AbstractTestBuilderResource <T, A extends Api> implements TestBuilderResource <T> {
  
  private ApiClient apiClient;
  private List<T> closables = new ArrayList<>();
  
  /**
   * Constructor
   * 
   * @param apiClient API client
   */
  public AbstractTestBuilderResource(ApiClient apiClient) {
    this.apiClient = apiClient;
  }
  
  @Override
  public T addClosable(T t) {
    closables.add(t);
    return t;
  }
  
  /**
   * Removes a closable from clean queue
   * 
   * @param predicate filter predicate
   */
  public void removeClosable(Predicate<? super T> predicate) {
    closables = closables.stream().filter(predicate).collect(Collectors.toList());
  }
  
  @Override
  public void close() throws Exception {
    for (int i = closables.size() - 1; i >= 0; i--) {
      clean(closables.get(i));
    }
  }

  /**
   * Builds API client
   * 
   * @return API client
   */
  protected A getApi() {
    return apiClient.buildClient(getApiClass());    
  }
  
  /**
   * Returns API class from generic type arguments
   * 
   * @return API class
   */
  protected Class<A> getApiClass() {
    return getTypeArgument((ParameterizedType) getClass().getGenericSuperclass(), 1);
  }
  
  /**
   * Returns nth type argument from parameterized type
   * 
   * @param parameterizedType parameterized type
   * @param index index of argument
   * @return type argument
   */
  @SuppressWarnings("unchecked")
  private <R> Class<R> getTypeArgument(ParameterizedType parameterizedType, int index) {
    return (Class<R>) parameterizedType.getActualTypeArguments()[index];
  }
  
}