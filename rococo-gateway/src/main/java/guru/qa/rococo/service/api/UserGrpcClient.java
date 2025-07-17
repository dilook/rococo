package guru.qa.rococo.service.api;

import guru.qa.rococo.grpc.GetUserRequest;
import guru.qa.rococo.grpc.RococoUserServiceGrpc;
import guru.qa.rococo.grpc.UpdateUserRequest;
import guru.qa.rococo.grpc.User;
import guru.qa.rococo.model.UserJson;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserGrpcClient {

    private final RococoUserServiceGrpc.RococoUserServiceBlockingStub userServiceStub;

    public UserGrpcClient(RococoUserServiceGrpc.RococoUserServiceBlockingStub userServiceStub) {
        this.userServiceStub = userServiceStub;
    }

    public UserJson getUser(String username) {
        try {
            GetUserRequest request = GetUserRequest.newBuilder()
                    .setUsername(username)
                    .build();

            User response = userServiceStub.getUser(request);
            return convertFromGrpcUser(response);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error retrieving user: " + e.getStatus().getDescription(), e);
        }
    }

    public UserJson updateUser(UserJson user) {
        try {
            UpdateUserRequest.Builder requestBuilder = UpdateUserRequest.newBuilder()
                    .setUsername(user.username())
                    .setFirstname(user.firstname() != null ? user.firstname() : "")
                    .setLastname(user.lastname() != null ? user.lastname() : "");

            if (user.avatar() != null) {
                requestBuilder.setAvatar(user.avatar());
            }

            User response = userServiceStub.updateUser(requestBuilder.build());
            return convertFromGrpcUser(response);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error updating user: " + e.getStatus().getDescription(), e);
        }
    }

    private UserJson convertFromGrpcUser(User user) {
        return new UserJson(
                UUID.fromString(user.getId()),
                user.getUsername(),
                user.getFirstname().isEmpty() ? null : user.getFirstname(),
                user.getLastname().isEmpty() ? null : user.getLastname(),
                user.getAvatar().isEmpty() ? null : user.getAvatar()
        );
    }
}