package io.github.wtfjoke.lambda;

import io.github.wtfjoke.lambda.carbon.aware.computing.Country;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.time.ZonedDateTime;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class LambdaHandlerTest {

    @Inject
    SsmClient ssm;

    @Test
    public void testSimpleLambdaSuccess() throws Exception {
        // you test your lambdas by invoking on http://localhost:8081
        // this works in dev mode too
        ssm.putParameter(r -> r.name("/carbon-aware-computing/api-key").value("SecretApiKey").type("SecureString"));
        CarbonAwareTimeWindowPayload in = new CarbonAwareTimeWindowPayload(Country.de, ZonedDateTime.now());
        given()
                .contentType("application/json")
                .accept("application/json")
                .body(in)
                .when()
                .post()
                .then()
                .statusCode(200);
    }

}
