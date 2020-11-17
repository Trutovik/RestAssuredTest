package com.sabal.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class TestCreateUser {

    private final String CONTEXT_PATH = "/spring5webapp";

    @BeforeEach
    void sertUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8888;
    }

    @Test
    final void testCreateUser() {

        List<Map<String, Object>> userAddresses = new ArrayList<>();

        HashMap<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Gomel");
        shippingAddress.put("country", "Belarus");
        shippingAddress.put("streetName", "Zhemchuzhnaya street, 20-60");
        shippingAddress.put("postalCode", "246013");
        shippingAddress.put("type", "shipping");

        HashMap<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city", "Gomel");
        billingAddress.put("country", "Belarus");
        billingAddress.put("streetName", "Zhemchuzhnaya street, 20-60");
        billingAddress.put("postalCode", "246013");
        billingAddress.put("type", "billing");
        userAddresses.add(shippingAddress);
        userAddresses.add(billingAddress);

        HashMap<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Aliaksandr");
        userDetails.put("lastName", "Sabalevich");
        userDetails.put("email", "casbah@mail.ru");
        userDetails.put("password", "123");
        userDetails.put("addresses", userAddresses);

        Response response = given().contentType("application/json")
                .accept("application/json")
                .body(userDetails)
                .when()
                .post(CONTEXT_PATH + "/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String userId = response.jsonPath().getString("userId");
        assertNotNull(userId);
        assertTrue(userId.length() == 30);

        String bodyString = response.body().asString();
        try {
            JSONObject responseBodyString = new JSONObject(bodyString);
            JSONArray addresses = responseBodyString.getJSONArray("addresses");
            assertNotNull(addresses);
            assertTrue(addresses.length() == 2);
            String addressId = addresses.getJSONObject(0).getString("addressId");
            assertNotNull(addressId);
            assertTrue(addressId.length() == 30);
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }
}
