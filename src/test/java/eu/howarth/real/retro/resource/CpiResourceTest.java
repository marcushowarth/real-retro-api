package eu.howarth.real.retro.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class CpiResourceTest {

    @Test
    void listsOneEntryPerYearFrom1988() {
        // CPI (D7BT) starts a year later than RPI (CHAW) — 1988, not 1987.
        given()
                .when().get("/api/cpi")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].year", equalTo(1988))
                .body("[0].index", notNullValue());
    }

    @Test
    void latestYearMatchesTheNewestEntryInTheList() {
        int latestFromList = given()
                .when().get("/api/cpi")
                .then().extract().jsonPath().getList("year", Integer.class)
                .stream().max(Integer::compareTo).orElseThrow();

        given()
                .when().get("/api/cpi/latest-year")
                .then()
                .statusCode(200)
                .body("year", equalTo(latestFromList));
    }
}
