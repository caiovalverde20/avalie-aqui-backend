package Avalieaqui.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String body = request.get("body");

        emailService.sendEmail("avalieaqui23.2@gmail.com", subject, body);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email enviado com sucesso!");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
