package steps.user;

import dto.UserDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static utils.BaseHelper.LOGIN_USER_ENDPOINT;

public class LoginUserSteps {
    @Step("Выполнить авторизацию пользователя")
    public static Response loginUser(UserDto user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(LOGIN_USER_ENDPOINT);
    }
}
