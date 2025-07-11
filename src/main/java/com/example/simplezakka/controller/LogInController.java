import com.example.simplezakka.dto.Login.LoginInfo;
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
    
    
    public LogInController(AuthService loginService) {
        this.loginService = loginService;
    }
    
   
    
    @GetMapping("/{users}")
    public ResponseEntity<LoginInfo> getUsersById(@PathVariable String email) {
        LoginInfo user = loginService.findById(email);
        if (email == null) {
            throw new Error("User not found with email: " + email);
        }
        return ResponseEntity.ok(email);
    }
}