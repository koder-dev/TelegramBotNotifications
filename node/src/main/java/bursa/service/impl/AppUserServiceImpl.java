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

@Service
@Log4j
public class AppUserServiceImpl implements AppUserService {
    private AppUserRepo appUserRepo;
    private CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailUri;

    public AppUserServiceImpl(AppUserRepo appUserRepo, CryptoTool cryptoTool) {
        this.appUserRepo = appUserRepo;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser user) {
        if (user.getIsActive()) {
            return "You are already registered";
        } else if (Objects.nonNull(user.getEmail())) {
            return "We already sent a confirmation link to your email.\nPlease go to link to activate account.";
        }
        user.setUserState(UserState.WAIT_FOR_EMAIL);
        appUserRepo.save(user);
        return "Write your email to activate account.";
    }

    @Override
    public String setEmail(AppUser user, String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Incorrect email address.For cancellation write /cancel";
        }
        var optionalUser = appUserRepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            user.setEmail(email);
            user.setUserState(UserState.BASIC_STATE);
            user = appUserRepo.save(user);

            var cryptoUserId = cryptoTool.hashOf(user.getId());
            var response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                String msg = String.format("Sending a letter error to email %s", email);
                log.error(msg);
                user.setEmail(null);
                appUserRepo.save(user);
                return msg;
            }
            return "We sent a confirmation link to your email.";
        } else {
            return "This email is already registered";
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
