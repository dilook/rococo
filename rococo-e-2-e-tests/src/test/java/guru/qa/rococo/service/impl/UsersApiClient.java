package guru.qa.rococo.service.impl;

import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.core.RestClient.EmtyRestClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.UserGrpcClient;
import guru.qa.rococo.service.UsersClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserGrpcClient userGrpcClient = new UserGrpcClient();

    @Override
    @Step("Создать пользователя с именем '{0}' через REST API")
    @NotNull
    public UserJson createUser(String username, String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
            UserJson createdUser = userGrpcClient.getUser(username);

            return createdUser.addTestData(new TestData(password));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
