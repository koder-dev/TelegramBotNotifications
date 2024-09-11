package bursa.controller;

import bursa.service.MailSenderService;
import bursa.utils.dto.MailParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static bursa.string.MailTextConst.MAIL_SENT_OK_RESPONSE_TEXT;

@RestController
@RequestMapping("/mail")
public class MailController {
    private final MailSenderService mailSenderService;

    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody MailParams mailParams) {
        mailSenderService.send(mailParams);
        return ResponseEntity.ok(MAIL_SENT_OK_RESPONSE_TEXT);
    }
}
