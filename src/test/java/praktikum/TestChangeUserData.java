package praktikum;

import org.example.praktikum.User;
import org.example.praktikum.UserLogic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class TestChangeUserData {
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
    public void tearDown() {
        String token = userLogic.loginUser(user)
                .extract().body().path("accessToken");
        user.setAccessToken(token);

        if (user.getAccessToken() != null) {
            userLogic.deleteUser(user);
        }
    }

    @Test
    public void changingEmailWithAuth() {
        String newEmail = UUID.randomUUID() + "@ya.ru";
        userLogic.changeEmailWithAuth(user, newEmail)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(newEmail));
    }

    @Test
    public void changingNameWithAuth() {
        String newName = String.valueOf(UUID.randomUUID());
        userLogic.changeNameWithAuth(user, newName)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(newName));
    }

    @Test
    public void changingEmailWithoutAuth(){
        String newEmail = UUID.randomUUID() + "@ya.ru";
        userLogic.changeEmailWithoutAuth(user, newEmail)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    public void changingUsersNameWithoutAuth() {
        String newName = String.valueOf(UUID.randomUUID());
        userLogic.changeNameWithoutAuth(user, newName)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}