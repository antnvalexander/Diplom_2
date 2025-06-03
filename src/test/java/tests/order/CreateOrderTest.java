package tests.order;

import dto.IngredientsDto;
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
import static org.hamcrest.core.IsNull.nullValue;
import static steps.order.CreateOrderSteps.*;
import static steps.user.CreateUserSteps.createUser;
import static steps.user.CreateUserSteps.getClearAccessToken;
import static steps.user.DeleteUserSteps.deleteUser;
import static utils.BaseHelper.BASE_URL;

public class CreateOrderTest {

    String locale;
    String name;
    String password;
    String email;
    String accessToken;
    UserDto user1;
    Response ingredients;


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

        ingredients = getIngredients();
    }

    @After
    public void clear() {
        deleteUser(accessToken)
                .then()
                .statusCode(202);
    }

    @DisplayName("Проверка создания заказа с авторизацией")
    @Description("Авторизация пользователя и дальнейшее создание заказа")
    @Test
    public void testSuccessfulCreateOrderWithAuth() {
        String firstIngredient = "Флюоресцентная булка R2-D3";
        String secondIngredient = "Биокотлета из марсианской Магнолии";
        String firstIngredientId = getIngredientId(firstIngredient, ingredients);
        String secondIngredientId = getIngredientId(secondIngredient, ingredients);

        createOrder(new IngredientsDto(new String[]{firstIngredientId, secondIngredientId}), accessToken).then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients[0].name", equalTo(firstIngredient))
                .body("order.ingredients[0]._id", equalTo(firstIngredientId))
                .body("order.ingredients[1].name", equalTo(secondIngredient))
                .body("order.ingredients[1]._id", equalTo(secondIngredientId))
                .body("order.status", equalTo("done"));
    }

    @DisplayName("Проверка создания заказа без авторизации")
    @Description("Авторизация пользователя не происходит, выполняется создание заказа")
    @Test
    public void testSuccessfulCreateOrderWithoutAuth() {
        String firstIngredient = "Флюоресцентная булка R2-D3";
        String secondIngredient = "Биокотлета из марсианской Магнолии";
        String firstIngredientId = getIngredientId(firstIngredient, ingredients);
        String secondIngredientId = getIngredientId(secondIngredient, ingredients);

        createOrder(new IngredientsDto(new String[]{firstIngredientId, secondIngredientId}), "").then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.status", nullValue())
                .body("order.ingredients[0].name", nullValue())
                .body("order.ingredients[0]._id", nullValue())
                .body("order.ingredients[1].name", nullValue())
                .body("order.ingredients[1]._id", nullValue())
                .body("order.status", nullValue());
    }

    @DisplayName("Проверка создания заказа без ингредиентов")
    @Description("Создали заказ с пустыми списком ингредиентов")
    @Test
    public void testCreateOrderWithoutIngredients() {
        createOrder(new IngredientsDto(new String[]{}), accessToken).then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @DisplayName("Проверка создания заказа без ингредиентов")
    @Description("Создали заказ с пустыми списком ингредиентов")
    @Test
    public void testCreateOrderWithIncorrectIngredients() {
        String firstIncorrectIngredientId = "incorrectId";
        createOrder(new IngredientsDto(new String[]{firstIncorrectIngredientId}), accessToken).then()
                .statusCode(500);
    }
}
