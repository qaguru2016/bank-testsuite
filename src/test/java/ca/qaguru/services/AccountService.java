package ca.qaguru.services;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertTrue;

public class AccountService {

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

    public void getAccountById(int id, int expStatusCode, Map<String, Object> requestBody) {
        ValidatableResponse validatableResponse =
        given()
                .spec(requestSpecification)
                .when()
                .get("/"+id)
                .then()
                .log().all()
                .assertThat().statusCode(expStatusCode);

        if(expStatusCode == HttpStatus.SC_OK){
            validatableResponse
                    .body("id",equalTo(id))
                    .body("accountHolderName", is(requestBody.get("accountHolderName")))
                    .body("balance", is(((Number)requestBody.get("balance")).floatValue()));
        }

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

    public void deleteAccountById(int id) {
        given()
                .spec(requestSpecification)
                .when()
                .delete("/"+id)
                .then()
                .log().all()
                .assertThat().statusCode(HttpStatus.SC_OK);
    }

    public void getAllAccounts(List<Map<String, Object>> expAccounts) {
        ValidatableResponse validatableResponse =
                given()
                        .spec(requestSpecification)
                        .when()
                        .get()
                        .then()
                        .log().all()
                        .assertThat().statusCode(HttpStatus.SC_OK);
        ArrayList<Map<String,Object>> response = validatableResponse.extract().body().as(ArrayList.class);
        //Convert id from Double to int type
        for(Map<String,Object> map:response){
            if (map.containsKey("id") && map.get("id") instanceof Double) {
                // Replace the double value with its integer equivalent
                Double id = (Double) map.get("id");
                map.put("id", id.intValue());
            }
        }
        expAccounts.forEach(acc->assertTrue(response.contains(acc),"Account not present - "+ acc));

    }
}
