package guru.qa.rococo.controller;


import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.UserGrpcClient;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserGrpcClient userGrpcClient;

    public UserController(UserGrpcClient userGrpcClient) {
        this.userGrpcClient = userGrpcClient;
    }

    @GetMapping
    public UserJson getUser(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaim("sub");
        return userGrpcClient.getUser(username);
    }


    @PatchMapping
    public UserJson updateUser(@AuthenticationPrincipal Jwt principal, @Valid @RequestBody UserJson user) {
        String username = principal.getClaim("sub");
        return userGrpcClient.updateUser(user.addUsername(username));
    }




}
