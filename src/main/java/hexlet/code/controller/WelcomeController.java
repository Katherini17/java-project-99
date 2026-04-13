package hexlet.code.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Welcome", description = "Root and health check endpoints")
public class WelcomeController {

    @Operation(summary = "Get welcome message")
    @GetMapping(path = "/welcome")
    public String root() {
        return "Welcome to Spring";
    }
}
