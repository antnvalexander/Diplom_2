package tests.user;

import dto.UserDto;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import utils.DataGenerator;

import static org.hamcrest.core.IsEqual.equalTo;
import static steps.user.CreateUserSteps.createUser;
import static steps.user.CreateUserSteps.getClearAccessToken;
import static steps.user.DeleteUserSteps.deleteUser;
import static utils.BaseHelper.BASE_URL;

public class CreateUserTest {

    String locale;
    String name;
    String password;
    String email;
    String accessToken;

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URL;

        locale = "ru";
        DataGenerator.FakeUser fakeUser = DataGenerator.generateUser(locale);
        name = fakeUser.getName();
        password = fakeUser.getPassword();
        email = fakeUser.getEmail();
    }

    @DisplayName("Проверка создания пользователя")
    @Description("Создание пользователя с валидным заполнением полей")
    @Test
    public void testSuccessfulUserCreation() {
        Response response = createUser(new UserDto(name, password, email));
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));

        accessToken = getClearAccessToken(response);
        deleteUser(accessToken)
                .then()
                .statusCode(202);
    }

    @DisplayName("Проверка создания существующего пользователя")
    @Description("Создание пользователя с существующим name")
    @Test
    public void testUserCreationExist() {
        Response responseFirst = createUser(new UserDto(name, password, email));
        responseFirst.then()
                .statusCode(200)
                .body("success", equalTo(true));

        Response responseSecond = createUser(new UserDto(name, password, email));
        responseSecond.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
        accessToken = getClearAccessToken(responseFirst);
        deleteUser(accessToken)
                .then()
                .statusCode(202);
    }

    @DisplayName("Проверка создания пользователя без обязательного поля")
    @Description("Не заполнить поле name")
    @Test
    public void testUserCreationWithEmptyName() {
        Response response = createUser(new UserDto("", password, email));
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
