import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {

    @BeforeAll
    static void setUp(){
        // base uri that rest assured will use for tests.
        // note that its using api-gateway port.
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOKWithValidToken() {

        // json for /login endpoint.
        String loginPayload = """
                {
                "email": "testuser@test.com",
                "password": "password123"
                }
                """;

        Response response = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));

    }


    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin() {

        String loginPayload = """
                {
                "email": "invalidtestuser@test.com",
                "password": "wrongpassword1234"
                }
                """;

        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(401);

    }

}
