package eu.howarth.real.retro.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class RpiResourceTest {

    @Test
    void listsOneEntryPerYearFrom1987() {
        given()
                .when().get("/api/rpi")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].year", equalTo(1987))
                .body("[0].index", notNullValue());
    }

    @Test
    void latestYearMatchesTheNewestEntryInTheList() {
        int latestFromList = given()
                .when().get("/api/rpi")
                .then().extract().jsonPath().getList("year", Integer.class)
                .stream().max(Integer::compareTo).orElseThrow();

        given()
                .when().get("/api/rpi/latest-year")
                .then()
                .statusCode(200)
                .body("year", equalTo(latestFromList));
    }
}
