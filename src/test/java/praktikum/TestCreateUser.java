package praktikum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.example.praktikum.User;
import org.example.praktikum.UserLogic;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class TestCreateUser {
    private User user;
    private UserLogic userLogic;

    @Before
    public void setUp(){
        userLogic = new UserLogic();

        String email = UUID.randomUUID() + "@ya.ru";
        String password = String.valueOf(UUID.randomUUID());
        String name = String.valueOf(UUID.randomUUID());

        user = new User(email, password, name);
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
    public void createUser(){
        userLogic.createUser(user)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    public void registrationWithoutName(){
        user.setName(null);
        userLogic.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void registrationWithoutEmail(){
        user.setEmail(null);
        userLogic.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void registrationWithoutPassword(){
        user.setPassword(null);
        userLogic.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void secondRegistration(){
        userLogic.createUser(user);

        userLogic.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false));
    }
}