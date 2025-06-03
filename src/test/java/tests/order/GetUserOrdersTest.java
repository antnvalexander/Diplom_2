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
import static steps.order.CreateOrderSteps.*;
import static steps.user.CreateUserSteps.createUser;
import static steps.user.CreateUserSteps.getClearAccessToken;
import static steps.user.DeleteUserSteps.deleteUser;
import static utils.BaseHelper.BASE_URL;

public class GetUserOrdersTest {
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

    @DisplayName("Получить список заказов пользователя")
    @Description("Авторизация пользователя и дальнейшее получение списка заказов")
    @Test
    public void testSuccessfulGetOrderListWithAuth() {
        String ingredient = "Флюоресцентная булка R2-D3";
        String ingredientId = getIngredientId(ingredient, ingredients);
        Response responseOrder = createOrder(new IngredientsDto(new String[]{ingredientId}), accessToken);
        String orderId = getOrderId(responseOrder);

        getUserOrders(accessToken).then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders[0]._id", equalTo(orderId));
    }


    @DisplayName("Получить список заказов пользователя без авторизации")
    @Description("Авторизация пользователя не выполняется, получение списка заказов невозможно")
    @Test
    public void testGetOrderListWithoutAuth() {
        getUserOrders("").then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
