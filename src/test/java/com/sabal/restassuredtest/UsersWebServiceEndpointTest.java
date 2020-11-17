package com.sabal.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersWebServiceEndpointTest {

    private final String CONTEXT_PATH = "/spring5webapp";
    private final String EMAIL_ADDRESS = "casbah@mail.ru";
    private final String JSON = "application/json";
    private static String authorizationHeader;
    private static String userIdHeader;
    private static List<Map<String, Object>> addresses;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8888;
    }

    /*
    *
    *  testUserLogin()
    *
    * */
    @Test
    final void a() {
        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", EMAIL_ADDRESS);
        loginDetails.put("password", "123");

        Response response = given().contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post(CONTEXT_PATH + "/users/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        authorizationHeader = response.header("Authorization");
        userIdHeader = response.header("UserID");
        assertNotNull(authorizationHeader);
        assertNotNull(userIdHeader);
    }

    /*
     *
     *  testGetUserDetails()
     *
     * */
    @Test
    final void b() {
        Response response = given()
                .pathParam("id", userIdHeader)
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .when()
                .get(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        addresses = response.jsonPath().getList("addresses");
        String addressId = (String) addresses.get(0).get("addressId");

        assertNotNull(userPublicId);
        assertNotNull(userEmail);
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertEquals(EMAIL_ADDRESS, userEmail);
        assertTrue(addresses.size() == 2);
        assertTrue(addressId.length() == 30);
    }

    /*
     *
     *  testUpdateUserDetails()
     *
     * */
    @Test
    @Disabled
    final void c() {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Victoria");
        userDetails.put("lastName", "Starakozhava");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization", authorizationHeader)
                .pathParam("id", userIdHeader)
                .body(userDetails)
                .when()
                .put(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        List<Map<String, Object>> storedAddresses = response.jsonPath().getList("addresses");

        assertNotNull(storedAddresses);
        assertEquals("Victoria", firstName);
        assertEquals("Starakozhava", lastName);
        assertTrue(addresses.size() == storedAddresses.size());
        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
    }

    /*
     *
     *  testDeleteUserDetails()
     *
     * */
    @Test
    final void d() {
        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization", authorizationHeader)
                .pathParam("id", userIdHeader)
                .when()
                .delete(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");
        assertEquals("SUCCESS", operationResult);
    }
}
