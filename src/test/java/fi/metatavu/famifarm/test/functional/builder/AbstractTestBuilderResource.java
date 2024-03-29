package fi.metatavu.famifarm.test.functional.builder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.ApiClient.Api;
import fi.metatavu.famifarm.client.auth.HttpBearerAuth;

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
    assertTrue(compareResult.passed(), compareResult.getMessage());    
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
    Customization sowingDateCustomization = new Customization("data.sowingDate", new ValueMatcher<Object>(){

      @Override
      public boolean equal(Object o1, Object o2) {
        OffsetDateTime date = OffsetDateTime.parse((String) o1);
        OffsetDateTime date2 = OffsetDateTime.parse((String) o2);
        return date.isEqual(date2);
      }

    });
    CustomComparator customComparator = new CustomComparator(JSONCompareMode.LENIENT, sowingDateCustomization);
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
    HttpBearerAuth bearerAuth = (HttpBearerAuth) apiClient.getAuthorization("BearerAuth");
    String token = bearerAuth.getBearerToken();
    return getBinaryData(String.format("Bearer %s", token), url);
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
  protected static ObjectMapper getObjectMapper() {
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