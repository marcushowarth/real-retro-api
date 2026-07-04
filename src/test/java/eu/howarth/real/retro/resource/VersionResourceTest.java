package eu.howarth.real.retro.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class VersionResourceTest {

    @Test
    void exposesBuildVersionInfo() {
        given()
                .when().get("/api/version")
                .then()
                .statusCode(200)
                .body("version", notNullValue())
                .body("gitSha", equalTo("dev"))    // default until the container deploy overrides it
                .body("builtAt", equalTo("dev"));
    }
}
