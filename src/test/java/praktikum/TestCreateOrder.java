package praktikum;
import io.restassured.response.ValidatableResponse;
import org.example.praktikum.Order;
import org.example.praktikum.OrderLogic;
import org.example.praktikum.User;
import org.example.praktikum.UserLogic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class TestCreateOrder {
    private final OrderLogic orderLogic = new OrderLogic();
    private final UserLogic userLogic = new UserLogic();
    private User user;

    @Before
    public void setUp() {
        String email = UUID.randomUUID() + "@ya.ru";
        String password = String.valueOf(UUID.randomUUID());
        String name = String.valueOf(UUID.randomUUID());

        user = new User(email, password, name);
        userLogic.createUser(user);
    }

    @After
    public void tearDown(){
        String token = userLogic.loginUser(user)
                .extract().body().path("accessToken");
        user.setAccessToken(token);

        if (user.getAccessToken() != null) {
            userLogic.deleteUser(user);
        }
    }

    @Test
    public void orderWithIngredientsAndAuth() {
        ValidatableResponse loginResult = userLogic
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

        String token = loginResult.extract().path("accessToken");

        String ingredient1 = orderLogic.getIngredients().extract().path("data[1]._id");
        String ingredient2 = orderLogic.getIngredients().extract().path("data[2]._id");
        String ingredient3 = orderLogic.getIngredients().extract().path("data[3]._id");
        List<String> ingredients = List.of(ingredient1, ingredient2, ingredient3);
        Order order = new Order(ingredients);
        ValidatableResponse orderResult = orderLogic.createOrder(token, order)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));

        List<String> finIngredients = orderResult
                .extract()
                .path("order.ingredients._id");

        assertThat("Ингредиенты не совпадают",
                finIngredients, containsInAnyOrder(ingredients.toArray()));
    }

    @Test
    public void orderWithoutIngredientsWithAuth(){
        ValidatableResponse validatableResponse = userLogic
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        String token = validatableResponse
                .extract()
                .path("accessToken");
        orderLogic.createOrderWithoutIngredients(token)
                .assertThat()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void orderWithIncorrectIngredientsUUID(){
        ValidatableResponse loginResult = userLogic
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

        String token = loginResult.extract().path("accessToken");
        orderLogic.getIngredients()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));

        String ingredient1 = String.valueOf(UUID.randomUUID());
        String ingredient2 = String.valueOf(UUID.randomUUID());
        String ingredient3 = String.valueOf(UUID.randomUUID());
        List<String> ingredients = List.of(ingredient1, ingredient2, ingredient3);
        Order order = new Order(ingredients);
        orderLogic.createOrder(token, order)
                .assertThat()
                .statusCode(500);
    }

    @Test
    public void orderWithIngredientsAndWithoutAuth(){
        String ingredient1 = orderLogic.getIngredients().extract().path("data[1]._id");
        String ingredient2 = orderLogic.getIngredients().extract().path("data[2]._id");
        String ingredient3 = orderLogic.getIngredients().extract().path("data[3]._id");
        List<String> ingredients = List.of(ingredient1, ingredient2, ingredient3);
        orderLogic.createOrderWithoutAuth(ingredients)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }
}