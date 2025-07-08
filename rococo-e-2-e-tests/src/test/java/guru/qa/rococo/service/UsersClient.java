package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.impl.UsersApiClient;
import guru.qa.rococo.service.impl.UsersDbClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UsersClient {

    static UsersClient getInstance() {
        return "api".equals(System.getProperty("client.impl"))
                ? new UsersApiClient()
                : new UsersDbClient();
    }

    @Nonnull
    UserJson createUser(String username, String password);
}
