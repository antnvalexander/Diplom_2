package steps.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static utils.BaseHelper.CHANGE_USER_INFO_ENDPOINT;

public class ChangeUserSteps {
    @Step("Отредактировать пользователя")
    public static Response changeUserName(String fieldName, String fieldValue, String accessToken) {
        return given()
                .contentType("application/json")
                .auth().oauth2(accessToken)
                .body(String.format("{\"%s\": \"%s\"}",
                        fieldName, fieldValue))
                .when()
                .patch(CHANGE_USER_INFO_ENDPOINT);
    }
}
