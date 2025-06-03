package steps.order;

import dto.IngredientsDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;
import static utils.BaseHelper.*;

public class CreateOrderSteps {
    @Step("Получить id ингредиента {ingredient}")
    public static String getIngredientId(String ingredient, Response response) {
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        JSONArray dataArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            if (item.getString("name").equalsIgnoreCase(ingredient)) {
                return item.getString("_id");
            }
        }
        return null;
    }

    @Step("Получить список ингредиентов")
    public static Response getIngredients() {
        return given()
                .contentType("application/json")
                .when()
                .get(GET_INGREDIENTS_ENDPOINT);
    }

    @Step("Создать заказ")
    public static Response createOrder(IngredientsDto ingredients, String accessToken) {
        return given()
                .contentType("application/json")
                .auth().oauth2(accessToken)
                .body(ingredients)
                .when()
                .post(CREATE_ORDER_ENDPOINT);
    }

    @Step("Получить id заказа")
    public static String getOrderId(Response response) {
        return response.path("order._id").toString();
    }

    @Step("Получить список всех заказов пользователя")
    public static Response getUserOrders(String accessToken) {
        return given()
                .contentType("application/json")
                .auth().oauth2(accessToken)
                .when()
                .get(GET_USER_ORDERS_ENDPOINT);
    }
}
