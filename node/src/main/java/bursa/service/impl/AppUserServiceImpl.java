package bursa.service.impl;

import bursa.entities.AppUser;
import bursa.enums.UserState;
import bursa.repositories.AppUserRepo;
import bursa.service.AppUserService;
import bursa.utils.CryptoTool;
import bursa.utils.dto.MailParams;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Objects;

import static bursa.model.RabbitQueue.REGISTRATION_MAIL_MESSAGE;
import static bursa.service.strings.NodeModuleStringConstants.*;

@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepo appUserRepo;
    private final CryptoTool cryptoTool;
    private final RabbitTemplate rabbitTemplate;

    public AppUserServiceImpl(AppUserRepo appUserRepo, CryptoTool cryptoTool, RabbitTemplate rabbitTemplate) {
        this.appUserRepo = appUserRepo;
        this.cryptoTool = cryptoTool;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public String registerUser(AppUser user) {
        if (Boolean.TRUE.equals(user.getIsActive())) {
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
            sendRegistrationMail(cryptoUserId, email);
            return LETTER_SENT_TEXT;
        } else {
            return EMAIL_IS_ALREADY_REGISTERED_TEXT;
        }
    }

    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(REGISTRATION_MAIL_MESSAGE, mailParams);
    }
}
