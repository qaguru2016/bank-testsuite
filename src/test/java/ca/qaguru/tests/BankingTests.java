package ca.qaguru.tests;

import ca.qaguru.lib.TestBase;

import ca.qaguru.services.AccountService;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BankingTests extends TestBase {
    @Test
    public void addAccountTest(){
        Faker faker= new Faker();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountHolderName", faker.name().firstName());
        requestBody.put("balance", faker.number().numberBetween(1000,50000));

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
        System.out.println("Id : " + id);
    }

    @Test
    public void GetAnAccountByIdTest(){
        Faker faker= new Faker();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountHolderName", faker.name().firstName());
        requestBody.put("balance", faker.number().randomDouble(2,1000,50000));

        AccountService accountService = new AccountService(requestSpecification);
        int id  = accountService.addAccount(requestBody);
        accountService.getAccountById(id, HttpStatus.SC_OK, requestBody);
    }
    @Test
    public void deposit(){
        Faker faker= new Faker();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountHolderName", faker.name().firstName());
        requestBody.put("balance", faker.number().randomDouble(2,1000,50000));

        AccountService accountService = new AccountService(requestSpecification);
        //Add account
        int id  = accountService.addAccount(requestBody);
        //Deposit
        float depositAmt = 500f;
        accountService.deposit(id,depositAmt);

        //Get Account by id
        requestBody.put("balance",((Number)requestBody.get("balance")).floatValue()+depositAmt);
        accountService.getAccountById(id, HttpStatus.SC_OK, requestBody);

    }
    @Test
    public void withdraw(){
        Faker faker= new Faker();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountHolderName", faker.name().firstName());
        requestBody.put("balance", faker.number().randomDouble(2,1000,50000));

        AccountService accountService = new AccountService(requestSpecification);
        //Add account
        int id  = accountService.addAccount(requestBody);
        //Withdraw
        float withdrawalAmt = 500f;
        accountService.withdraw(id,withdrawalAmt);

        //Get Account by id
        requestBody.put("balance",((Number)requestBody.get("balance")).floatValue()-withdrawalAmt);
        accountService.getAccountById(id, HttpStatus.SC_OK, requestBody);

    }
    @Test
    public void deleteAccount(){
        Faker faker= new Faker();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountHolderName", faker.name().firstName());
        requestBody.put("balance", faker.number().randomDouble(2,1000,50000));

        AccountService accountService = new AccountService(requestSpecification);
        //Add account
        int id  = accountService.addAccount(requestBody);
        //Delete Account
        accountService.deleteAccountById(id);

        //Get Account by id
        accountService.getAccountById(id,HttpStatus.SC_INTERNAL_SERVER_ERROR,null);
    }

}
