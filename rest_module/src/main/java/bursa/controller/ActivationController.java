package bursa.controller;

import bursa.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class ActivationController {
    private final UserActivationService userActivationService;

    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    @GetMapping("/activation")
    public ResponseEntity<String> activateUser(@RequestParam("id") String id) {
        boolean res = userActivationService.activateUser(id);
        if (res) {
            return ResponseEntity.ok("Registration complete successfully");
        }
        return ResponseEntity.internalServerError().build();
    }
}
