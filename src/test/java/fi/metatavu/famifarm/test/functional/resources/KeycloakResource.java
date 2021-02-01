package fi.metatavu.famifarm.test.functional.resources;

import java.util.HashMap;
import java.util.Map;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import fi.metatavu.famifarm.test.functional.builder.auth.DefaultAccessTokenProvider;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class KeycloakResource implements QuarkusTestResourceLifecycleManager {

    static KeycloakContainer keycloak = new KeycloakContainer()
        .withRealmImportFile("test-realm.json");

    @Override
    public Map<String, String> start() {
        keycloak.start();
        DefaultAccessTokenProvider.AUTH_SERVER_URL = keycloak.getAuthServerUrl();
        Map<String, String> config = new HashMap<>();
        config.put("quarkus.oidc.auth-server-url", String.format("%s/realms/test", keycloak.getAuthServerUrl()));
        config.put("quarkus.oidc.client-id", "api");
        config.put("quarkus.oidc.credentials.secret", "041e7b11-70a1-4beb-a59a-c1de3fde1cf8");
        config.put("famifarm.keycloak.admin.secret", "cfca0d98-3aa3-4ee6-bb44-a72df4c02d1a"); 
        config.put("famifarm.keycloak.admin.client", "admin-cli"); 
        config.put("famifarm.keycloak.admin.password", "api-admin"); 
        config.put("famifarm.keycloak.admin.user", "api-admin"); 
        config.put("famifarm.keycloak.admin.realm", "test");
        config.put("famifarm.keycloak.admin.url", keycloak.getAuthServerUrl());
        return config;
    }

    @Override
    public void stop() {
        keycloak.stop();
    }
    
}
