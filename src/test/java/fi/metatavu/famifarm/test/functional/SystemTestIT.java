package fi.metatavu.famifarm.test.functional;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for System
 */
@QuarkusTest
public class SystemTestIT {

    @Test
    public void testPingEndpoint() {
        RestAssured.given()
                .when().get("http://localhost:8081/v1/system/ping")
                .then()
                .statusCode(200)
                .body(CoreMatchers.is("pong"));
    }
}
