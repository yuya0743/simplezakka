import com.example.simplezakka.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class LogInController {

    private final AuthService loginService;
    
    @Autowired
    public loginController(AuthService loginService) {
        this.loginService = loginService;
    }
    
   
    
    @GetMapping("/{users}")
    public ResponseEntity<users> getUsersById(@PathVariable String email) {
        LoginDetail email = loginService.findUserstByemail(email);
        if (email == null) {
            throw new Error("User not found with email: " + email);
        }
        return ResponseEntity.ok(UserId);
    }
}