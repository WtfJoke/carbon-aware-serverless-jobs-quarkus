package io.github.wtfjoke.lambda;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import java.time.ZonedDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class LambdaHandlerTest {

    @Test
    public void testSimpleLambdaSuccess() throws Exception {
        // you test your lambdas by invoking on http://localhost:8081
        // this works in dev mode too

        CarbonAwareTimeWindowPayload in = new CarbonAwareTimeWindowPayload(Location.de, ZonedDateTime.now());
        given()
                .contentType("application/json")
                .accept("application/json")
                .body(in)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body(containsString("10"));
    }

}
