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
import static steps.user.ChangeUserSteps.changeUserName;
import static steps.user.CreateUserSteps.createUser;
import static steps.user.CreateUserSteps.getClearAccessToken;
import static steps.user.DeleteUserSteps.deleteUser;
import static utils.BaseHelper.BASE_URL;

public class ChangeUserInfoTest {
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

    @DisplayName("Проверка изменения имени пользователя с авторизацией")
    @Description("Авторизация пользователя и дальнейшее изменение данных пользователя - поле Name")
    @Test
    public void testSuccessfulUserChangeName() {
        String newName = DataGenerator.generateUser(locale).getName();
        changeUserName("name", newName, accessToken).then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(newName));
    }

    @DisplayName("Проверка изменения email пользователя с авторизацией")
    @Description("Авторизация пользователя и дальнейшее изменение данных пользователя - поле Email")
    @Test
    public void testSuccessfulUserChangeEmail() {
        String newEmail = DataGenerator.generateUser(locale).getEmail();
        changeUserName("email", newEmail, accessToken).then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(name));
    }


    @DisplayName("Проверка изменения password пользователя с авторизацией")
    @Description("Авторизация пользователя и дальнейшее изменение данных пользователя - поле Password")
    @Test
    public void testSuccessfulUserChangePassword() {
        String newPass = DataGenerator.generateUser(locale).getPassword();
        changeUserName("password", newPass, accessToken).then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @DisplayName("Проверка изменения Имени пользователя без авторизацией")
    @Description("Изменение данных пользователя без авторизации - поле Name")
    @Test
    public void testUserChangeNameWithoutAuth() {
        String newName = DataGenerator.generateUser(locale).getName();
        changeUserName("name", newName, "").then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }


    @DisplayName("Проверка изменения email пользователя с авторизацией")
    @Description("Авторизация пользователя и дальнейшее изменение данных пользователя - поле Email")
    @Test
    public void testUserChangeEmailWithoutAuth() {
        String newEmail = DataGenerator.generateUser(locale).getEmail();
        changeUserName("email", newEmail, "").then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }


    @DisplayName("Проверка изменения password пользователя с авторизацией")
    @Description("Авторизация пользователя и дальнейшее изменение данных пользователя - поле Password")
    @Test
    public void testUserChangePassWithoutAuth() {
        String newPass = DataGenerator.generateUser(locale).getPassword();
        changeUserName("password", newPass, "").then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
