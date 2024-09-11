package bursa.service.impl;

import bursa.entities.AppUser;
import bursa.enums.UserState;
import bursa.repositories.AppUserRepo;
import bursa.service.AppUserService;
import bursa.utils.CryptoTool;
import bursa.utils.dto.MailParams;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Objects;

import static bursa.service.strings.NodeModuleStringConstants.*;

@Service
@Log4j
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepo appUserRepo;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailUri;

    public AppUserServiceImpl(AppUserRepo appUserRepo, CryptoTool cryptoTool) {
        this.appUserRepo = appUserRepo;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser user) {
        if (user.getIsActive()) {
            return USER_ALREADY_REGISTERED_TEXT;
        } else if (Objects.nonNull(user.getEmail())) {
            return MAIL_ALREADY_SENT_TEXT;
        }
        user.setUserState(UserState.WAIT_FOR_EMAIL);
        appUserRepo.save(user);
        return WRITE_YOUR_MAIL_TEXT;
    }

    @Override
    public String setEmail(AppUser user, String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return INCORRECT_EMAIL_TEXT;
        }
        var optionalUser = appUserRepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            user.setEmail(email);
            user.setUserState(UserState.BASIC_STATE);
            user = appUserRepo.save(user);

            var cryptoUserId = cryptoTool.hashOf(user.getId());
            var response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                String msg = String.format(SENDING_LETTER_ERROR_TEXT, email);
                log.error(msg);
                user.setEmail(null);
                appUserRepo.save(user);
                return msg;
            }
            return LETTER_SENT_TEXT;
        } else {
            return EMAIL_IS_ALREADY_REGISTERED_TEXT;
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        var request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(mailUri, HttpMethod.POST, request, String.class);
    }
}
