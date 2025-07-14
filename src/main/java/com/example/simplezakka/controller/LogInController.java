import com.example.simplezakka.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class LoginController {

    private final LoginService loginService;
    
    @Autowired
    public loginController(LoginService loginService) {
        this.loginService = loginService;
    }
    
   
    
    @GetMapping("/{users}")
    public ResponseEntity<users> getUsersById(@PathVariable String email) {
        LoginDetail email = loginService.findUserstByemail(email);
        if (UserId == null) {
            throw new Error('ログインに失敗しました');
        }
        return ResponseEntity.ok(UserId);
    }
}