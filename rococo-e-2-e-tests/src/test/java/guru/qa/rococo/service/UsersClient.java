package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.impl.UsersApiClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UsersClient {

    static UsersClient getInstance() {
        return new UsersApiClient();
    }

    @Nonnull
    UserJson createUser(String username, String password);
}
