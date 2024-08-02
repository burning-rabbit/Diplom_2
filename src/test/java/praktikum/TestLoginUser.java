package praktikum;

import org.example.praktikum.User;
import org.example.praktikum.UserLogic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class TestLoginUser {
    private User user;
    private UserLogic userLogic;

    @Before
    public void setUp() {
        userLogic = new UserLogic();

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
    public void login(){
        userLogic.loginUser(user)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    public void loginWithIncorrectEmail(){
        user.setEmail("incorrectEmail");
        userLogic.loginUser(user)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    public void loginWithIncorrectPassword(){
        user.setPassword("incorrectPassword");
        userLogic.loginUser(user)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}