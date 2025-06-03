package tests.user;

import dto.UserDto;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DataGenerator;

import static org.hamcrest.core.IsEqual.equalTo;
import static steps.user.CreateUserSteps.createUser;
import static steps.user.CreateUserSteps.getClearAccessToken;
import static steps.user.DeleteUserSteps.deleteUser;
import static steps.user.LoginUserSteps.loginUser;
import static utils.BaseHelper.BASE_URL;

public class LoginUserTest {
    String locale;
    String name;
    String password;
    String email;
    String accessToken;
    UserDto user1;


    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URL;

        locale = "ru";
        DataGenerator.FakeUser fakeUser = DataGenerator.generateUser(locale);
        name = fakeUser.getName();
        password = fakeUser.getPassword();
        email = fakeUser.getEmail();

        user1 = new UserDto(name, password, email);
        Response response = createUser(user1);
        accessToken = getClearAccessToken(response);

    }

    @After
    public void clear() {
        deleteUser(accessToken)
                .then()
                .statusCode(202);
    }

    @DisplayName("Проверка авторизации существующего пользователя")
    @Description("Авторизация пользователя с валидным заполнением полей")
    @Test
    public void testSuccessfulUserLogin() {
        loginUser(user1).then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @DisplayName("Проверка авторизации несуществующего пользователя")
    @Description("Авторизация пользователя с неправильным email")
    @Test
    public void testUserLoginWithIncorrectName() {
        String incorrectEmail = DataGenerator.generateUser(locale).getEmail();
        loginUser(new UserDto(name, password, incorrectEmail))
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
