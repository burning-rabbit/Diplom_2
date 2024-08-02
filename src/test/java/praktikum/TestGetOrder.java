package praktikum;

import io.restassured.response.ValidatableResponse;
import org.example.praktikum.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.example.praktikum.User;
import org.example.praktikum.UserLogic;
import org.example.praktikum.OrderLogic;

import java.util.List;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestGetOrder {
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
    public void getOrdersWithAuth(){
        ValidatableResponse validatableResponse = userLogic
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        String token = validatableResponse
                .extract()
                .path("accessToken");

        String ingredient1 = orderLogic.getIngredients().extract().path("data[1]._id");
        String ingredient2 = orderLogic.getIngredients().extract().path("data[2]._id");
        String Ingredient3 = orderLogic.getIngredients().extract().path("data[3]._id");
        List<String> ingredients = List.of(ingredient1, ingredient2, Ingredient3);
        Order order = new Order(ingredients);
        orderLogic.createOrder(token, order);
        orderLogic.getOrdersAuth(token)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    public void getOrderWithoutAuth(){
        orderLogic.getOrdersWithoutAuth()
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}