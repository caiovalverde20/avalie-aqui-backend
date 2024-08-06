package Avalieaqui.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String body = request.get("body");

        emailService.sendEmail("avalieaqui23.2@gmail.com", subject, body);

        return ResponseEntity.status(HttpStatus.OK).body("Email enviado com sucesso!");
    }
}
