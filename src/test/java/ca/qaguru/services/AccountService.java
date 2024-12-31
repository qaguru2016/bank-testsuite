package ca.qaguru.services;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AccountService {
    private int id;
    RequestSpecification requestSpecification;

    public AccountService(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public int addAccount(Map<String,Object> requestBody){
        ValidatableResponse validatableResponse =
                given()
                        .spec(requestSpecification)
                        .body(requestBody)
                .when()
                        .post()
                .then()
                        .log().all()
                        .assertThat()
                        .statusCode(HttpStatus.SC_CREATED);
        int id = validatableResponse.extract().response().jsonPath().getInt("id");
        return id;
    }

    public void getAccountById(int id, Map<String, Object> requestBody) {
        ValidatableResponse validatableResponse =
        given()
                .spec(requestSpecification)
                .when()
                .get("/"+id)
                .then()
                .log().all()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body("id",equalTo(id))
                .body("accountHolderName", is(requestBody.get("accountHolderName")))
                .body("balance", is(((Number)requestBody.get("balance")).floatValue()));

    }

    public void deposit(int id, float depositAmt) {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("amount",depositAmt);
        given()
                .spec(requestSpecification)
                .body(requestBody)
        .when()
                .put("/deposit/"+id)
        .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

    }

    public void withdraw(int id, float withdrawalAmt) {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("amount",withdrawalAmt);
        given()
                .spec(requestSpecification)
                .body(requestBody)
                .when()
                .put("/withdraw/"+id)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }
}
