package guru.qa.rococo.service;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.grpc.GetUserRequest;
import guru.qa.rococo.grpc.RococoUserServiceGrpc;
import guru.qa.rococo.grpc.UpdateUserRequest;
import guru.qa.rococo.grpc.User;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.Step;
import io.qameta.allure.grpc.AllureGrpc;

import java.util.UUID;


public class UserGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
            .intercept(new AllureGrpc())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final RococoUserServiceGrpc.RococoUserServiceBlockingStub userServiceStub = RococoUserServiceGrpc.newBlockingStub(channel);

    @Step("Получить пользователя '{username}' через gRPC")
    public UserJson getUser(String username) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername(username)
                .build();

        User response = userServiceStub.getUser(request);
        return convertFromGrpcUser(response);
    }

    @Step("Обновить пользователя через gRPC")
    public UserJson updateUser(UserJson user) {
        UpdateUserRequest.Builder requestBuilder = UpdateUserRequest.newBuilder()
                .setUsername(user.username())
                .setFirstname(user.firstname() != null ? user.firstname() : "")
                .setLastname(user.lastname() != null ? user.lastname() : "")
                .setAvatar(user.avatar() != null ? user.avatar() : "");

        User response = userServiceStub.updateUser(requestBuilder.build());
        return convertFromGrpcUser(response);
    }

    private UserJson convertFromGrpcUser(User user) {
        return new UserJson(
                UUID.fromString(user.getId()),
                user.getUsername(),
                user.getFirstname().isEmpty() ? null : user.getFirstname(),
                user.getLastname().isEmpty() ? null : user.getLastname(),
                user.getAvatar().isEmpty() ? null : user.getAvatar(),
                null
        );
    }
}