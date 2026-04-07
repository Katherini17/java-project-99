package hexlet.code.controller.api;

import hexlet.code.dto.AuthRequest;
import hexlet.code.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
@Tag(name = "Authentication", description = "Operations for user login and token generation")
public class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Authenticate user and get JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("")
    public String login(@RequestBody AuthRequest authRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        return tokenService.generateToken(authentication);
    }

}
