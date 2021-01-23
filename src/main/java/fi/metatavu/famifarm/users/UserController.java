package fi.metatavu.famifarm.users;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;

/**
 * User controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class UserController {
  
  @ConfigProperty(name = "famifarm.keycloak.admin.secret") 
  private String keycloakAdminClientSecret;

  @ConfigProperty(name = "famifarm.keycloak.admin.client") 
  private String keycloakAdminClientId;

  @ConfigProperty(name = "famifarm.keycloak.admin.password") 
  private String keycloakAdminPassword;

  @ConfigProperty(name = "famifarm.keycloak.admin.user") 
  private String keycloakAdminUser;

  @ConfigProperty(name = "famifarm.keycloak.admin.realm") 
  private String keycloakAdminRealm;

  @ConfigProperty(name = "famifarm.keycloak.admin.url") 
  private String keycloakAdminUrl;

  @Inject
  private Logger logger;

  /**
   * Returns user's display name 
   * 
   * @param userId user id
   * @return display name
   */
  public String getUserDisplayName(UUID userId) {
    try {
      Keycloak keycloakClient = getKeycloakClient();
      RealmResource realm = keycloakClient.realm(getRealm());
      
      UsersResource users = realm.users();
      UserResource userResource = users.get(userId.toString());
      if (userResource != null) {
        UserRepresentation representation = userResource.toRepresentation();
        if (representation != null) {
          return getUserDisplayName(representation);
        }
      }
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(String.format("Failed to resolve user %s display name", userId), e);
      }
    }
    
    return userId.toString();
  }

  /**
   * Formats user representation to display name
   * 
   * @param representation user representation
   * @return display name
   */
  private String getUserDisplayName(UserRepresentation representation) {
    String firstName = representation.getFirstName();
    String lastName = representation.getLastName();
    String email = representation.getEmail();
    
    if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
      return String.format("%s, %s", lastName, firstName);
    }
    
    if (StringUtils.isNotBlank(lastName)) {
      return lastName;
    }
 
    if (StringUtils.isNotBlank(firstName)) {
      return firstName;
    }
    
    return String.format("<%s>", email);
  }
  
  /**
   * Constructs a Keycloak client
   * 
   * @return Keycloak client 
   */
  private Keycloak getKeycloakClient() {
    String serverUrl = getServerUrl();
    String realm = getRealm();
    String adminUser = getAdminUser();
    String adminPassword = getAdminPassword();
    String clientId = getClientId();
    String clientSecret = getClientSecret();

    return KeycloakBuilder.builder()
      .serverUrl(serverUrl)
      .realm(realm)
      .username(adminUser)
      .password(adminPassword)
      .clientId(clientId)
      .clientSecret(clientSecret)
      .grantType(OAuth2Constants.PASSWORD)
      .build();
  }

  /**
   * Returns Keycloak client secret setting
   * 
   * @return Keycloak client secret setting
   */
  private String getClientSecret() {
    return keycloakAdminClientSecret;
  }

  /**
   * Returns Keycloak client id setting
   * 
   * @return Keycloak client id setting
   */
  private String getClientId() {
    return keycloakAdminClientId;
  }

  /**
   * Returns Keycloak admin password setting
   * 
   * @return Keycloak admin password setting
   */
  private String getAdminPassword() {
    return keycloakAdminPassword;
  }

  /**
   * Returns Keycloak admin user setting
   * 
   * @return Keycloak admin user setting
   */
  private String getAdminUser() {
    return keycloakAdminUser;
  }

  /**
   * Returns Keycloak realm setting
   * 
   * @return Keycloak realm  setting
   */
  private String getRealm() {
    return keycloakAdminRealm;
  }

  /**
   * Returns Keycloak server URL setting
   * 
   * @return Keycloak server URL setting
   */
  private String getServerUrl() {
    return keycloakAdminUrl;
  }
  
}