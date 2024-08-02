package org.example.praktikum;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserLogic extends URLs {

    public ValidatableResponse createUser(User user) {
        return RestAssured.given()
                .spec(getSpecification())
                .body(user)
                .post(CREATE_USER)
                .then();
    }


    private String getToken(User user){
        ValidatableResponse loginResponse = given()
                .spec(getSpecification())
                .body(user)
                .when()
                .post(LOGIN_USER)
                .then()
                .assertThat()
                .statusCode(200);

        String token = loginResponse.extract().path("accessToken");
        if (token == null) {
            throw new IllegalArgumentException("Token is null");
        }
        return token;
    }


    public ValidatableResponse loginUser(User user) {
        return RestAssured.given()
                .spec(getSpecification())
                .body(user)
                .post(LOGIN_USER)
                .then();
    }


    public ValidatableResponse changeEmailWithAuth(User user, String newEmail){
        String token = getToken(user);
        user.setEmail(newEmail);
        return given()
                .spec(getSpecification())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(USER)
                .then();
    }


    public ValidatableResponse changeNameWithAuth(User user, String newName) {
        String token = getToken(user);
        user.setName(newName);
        return given()
                .spec(getSpecification())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(USER)
                .then();
    }


    public ValidatableResponse changeEmailWithoutAuth(User user, String newEmail){
        user.setEmail(newEmail);
        return given()
                .spec(getSpecification())
                .body(user)
                .when()
                .patch(USER)
                .then();
    }


    public ValidatableResponse changeNameWithoutAuth(User user, String newName){
        user.setName(newName);
        return given()
                .spec(getSpecification())
                .body(user)
                .when()
                .patch(USER)
                .then();
    }

    public void deleteUser(User user){
        given()
                .spec(getSpecification())
                .header("accessToken", user.getAccessToken())
                .when()
                .delete(USER)
                .then();
    }
}
