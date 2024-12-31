package ca.qaguru.lib;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public class TestBase {
    protected final String baseUri = "http://localhost:8080";
    protected final String basePath = "/api/accounts";
    protected RequestSpecification requestSpecification;
    @BeforeSuite
    public void beforeSuite(){
        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .setBaseUri(baseUri)
                .setBasePath(basePath)
                .setContentType(ContentType.JSON)
                .build();
    }
}
