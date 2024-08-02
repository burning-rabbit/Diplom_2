package org.example.praktikum;

import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.Map;

public class OrderLogic extends URLs {

    public ValidatableResponse getIngredients() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(INGREDIENTS)
                .then();
    }


    public ValidatableResponse createOrder(String accessToken, Order order){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpecification())
                .body(order)
                .when()
                .post(ORDERS)
                .then();
    }


    public ValidatableResponse getOrdersAuth(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpecification())
                .when()
                .get(ORDERS)
                .then();
    }


    public ValidatableResponse createOrderWithoutIngredients(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpecification())
                .body("")
                .when()
                .post(ORDERS)
                .then();
    }


    public ValidatableResponse createOrderWithoutAuth(List<String> ingredients){
        Map<String, Object> requestMap = Map.of("ingredients", ingredients);
        return given()
                .spec(getSpecification())
                .body(requestMap)
                .when()
                .post(ORDERS)
                .then();
    }


    public ValidatableResponse getOrdersWithoutAuth(){
        return given()
                .spec(getSpecification())
                .when()
                .get(ORDERS)
                .then();
    }
}
