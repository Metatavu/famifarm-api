package fi.metatavu.famifarm.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.famifarm.ApiClient;
import fi.metatavu.famifarm.test.functional.settings.TestSettings;

/**
 * Default implementation of test builder authentication provider
 * 
 * @author Antti Leppä
 */
public class TestBuilderAuthentication extends AbstractTestBuilderAuthentication {
  
  private AccessTokenProvider accessTokenProvider;

  /**
   * Constructor
   * 
   * @param accessTokenProvider access token builder
   */
  public TestBuilderAuthentication(AccessTokenProvider accessTokenProvider) {
    this.accessTokenProvider = accessTokenProvider;
  }
  
  /**
   * Creates ApiClient authenticated by the given access token
   * 
   * @return ApiClient authenticated by the given access token
   * @throws IOException 
   */
  @Override
  protected ApiClient createClient() throws IOException {
    String accessToken = accessTokenProvider.getAccessToken();
    String authorization = accessToken != null ? String.format("Bearer %s", accessToken) : null;
    ApiClient apiClient = authorization != null ? new ApiClient("BearerAuth", authorization) : new ApiClient();
    String basePath = String.format("http://%s:%d", TestSettings.getHost(), TestSettings.getPort());
    apiClient.setBasePath(basePath);
    return apiClient;
  }
  
}
