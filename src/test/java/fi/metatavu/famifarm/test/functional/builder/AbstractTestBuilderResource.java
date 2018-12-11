package fi.metatavu.famifarm.test.functional.builder;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
    
    closables.clear();
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
   * Asserts that actual object equals expected object when both are serialized into JSON
   * 
   * @param expected expected
   * @param actual actual
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  protected void assertJsonsEqual(Object expected, Object actual) throws IOException, JSONException {
    JSONCompareResult compareResult = jsonCompare(expected, actual);
    assertTrue(compareResult.getMessage(), compareResult.passed());    
  }
  
  /**
   * Compares objects as serialized JSONs
   * 
   * @param expected expected
   * @param actual actual
   * @return comparison result
   * @throws JSONException
   * @throws JsonProcessingException
   */
  protected JSONCompareResult jsonCompare(Object expected, Object actual) throws JSONException, JsonProcessingException {
    CustomComparator customComparator = new CustomComparator(JSONCompareMode.LENIENT);
    return JSONCompare.compareJSON(toJSONString(expected), toJSONString(actual), customComparator);
  }  
  
  /**
   * Serializes an object into JSON
   * 
   * @param object object
   * @return JSON string
   * @throws JsonProcessingException
   */
  private String toJSONString(Object object) throws JsonProcessingException {
    if (object == null) {
      return null;
    }
    
    return getObjectMapper().writeValueAsString(object);
  }
  
  /**
   * Returns object mapper with default modules and settings
   * 
   * @return object mapper
   */
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    return objectMapper;
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