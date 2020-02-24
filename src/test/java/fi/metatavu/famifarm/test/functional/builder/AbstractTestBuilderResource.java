package fi.metatavu.famifarm.test.functional.builder;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
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
import fi.metatavu.famifarm.auth.ApiKeyAuth;

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
  private List<List<T>> closableLists = new ArrayList<>();
  
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
  
  @Override
  public List<T> addClosables(List<T> t) {
    closableLists.add(t);
    return t;
  }
  
  /**
   * Returns API client
   * 
   * @return API client
   */
  protected ApiClient getApiClient() {
    return apiClient;
  }
  
  /**
   * Removes a closable from clean queue
   * 
   * @param predicate filter predicate
   */
  public void removeClosable(Predicate<? super T> predicate) {
    closables = closables.stream().filter(predicate).collect(Collectors.toList());
  }
  
  /**
   * Removes a closable list from clean queue
   * 
   * @param predicate filter predicate
   */
  public void removeClosables(Predicate<? super T> predicate) {
    closableLists = closableLists.stream().map(closableList -> {
      return closableList.stream().filter(predicate).collect(Collectors.toList());
    }).collect(Collectors.toList());
  }
  
  @Override
  public void close() throws Exception {
    for (int i = closables.size() - 1; i >= 0; i--) {
      clean(closables.get(i));
    }
    
    for (int i = closableLists.size() - 1; i >= 0; i--) {
      for (int j = closableLists.get(i).size()- 1; j >= 0; j--) {
        clean(closableLists.get(i).get(j));
      }
      closableLists.get(i).clear();
    }
    
    closables.clear();
    closableLists.clear();
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
   * Download binary data using API client authorization
   * 
   * @param apiClient API client
   * @param url URL
   * @return downloaded data
   * @throws IOException thrown when downloading fails
   */
  protected byte[] getBinaryData(ApiClient apiClient, URL url) throws IOException {
    ApiKeyAuth bearerAuth = (ApiKeyAuth) apiClient.getAuthorization("BearerAuth");
    String authorization = bearerAuth.getApiKey();
    return getBinaryData(authorization, url);
  }  
  
  /**
   * Downloads binary data
   * 
   * @param authorization authorization header value 
   * @param url URL
   * @return downloaded data
   * @throws IOException thrown when downloading fails
   */
  protected byte[] getBinaryData(String authorization, URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization", authorization);
    connection.setDoOutput(true);
    
    try (InputStream inputStream = connection.getInputStream()) {
      return IOUtils.toByteArray(inputStream);      
    }
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