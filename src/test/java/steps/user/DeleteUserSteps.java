package steps.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static utils.BaseHelper.DELETE_USER_ENDPOINT;

public class DeleteUserSteps {
    @Step("Удалить пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .contentType("application/json")
                .when()
                .delete(DELETE_USER_ENDPOINT);
    }
}
