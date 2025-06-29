package guru.qa.rococo.test;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {

    @ApiLogin
    @Test
    @User
    void loginTest(@Token String token) {
        System.out.println(token);
    }
}
