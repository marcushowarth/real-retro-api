package eu.howarth.real.retro.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class IndexResourceTest {

    @Test
    void rpi_listsOneEntryPerYearFrom1987() {
        given()
                .when().get("/api/index/rpi")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].year", equalTo(1987))
                .body("[0].index", notNullValue());
    }

    @Test
    void rpi_latestYearMatchesTheNewestEntryInTheList() {
        int latestFromList = given()
                .when().get("/api/index/rpi")
                .then().extract().jsonPath().getList("year", Integer.class)
                .stream().max(Integer::compareTo).orElseThrow();

        given()
                .when().get("/api/index/rpi/latest-year")
                .then()
                .statusCode(200)
                .body("year", equalTo(latestFromList));
    }

    @Test
    void cpi_listsOneEntryPerYearFrom1988() {
        // CPI (D7BT) starts a year later than RPI (CHAW) — 1988, not 1987.
        given()
                .when().get("/api/index/cpi")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].year", equalTo(1988))
                .body("[0].index", notNullValue());
    }

    @Test
    void cpi_latestYearMatchesTheNewestEntryInTheList() {
        int latestFromList = given()
                .when().get("/api/index/cpi")
                .then().extract().jsonPath().getList("year", Integer.class)
                .stream().max(Integer::compareTo).orElseThrow();

        given()
                .when().get("/api/index/cpi/latest-year")
                .then()
                .statusCode(200)
                .body("year", equalTo(latestFromList));
    }

    @Test
    void unknownSeries_returnsNotFound() {
        given()
                .when().get("/api/index/gbp")
                .then()
                .statusCode(404);
    }
}
