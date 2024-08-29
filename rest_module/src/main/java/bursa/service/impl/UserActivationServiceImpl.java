package bursa.service.impl;

import bursa.entities.AppUser;
import bursa.repositories.AppUserRepo;
import bursa.service.UserActivationService;
import bursa.utils.CryptoTool;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserRepo appUserRepo;
    private final CryptoTool cryptoTool;

    public UserActivationServiceImpl(AppUserRepo appUserRepo, CryptoTool cryptoTool) {
        this.appUserRepo = appUserRepo;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activateUser(String hashUserId) {
        Long userId = cryptoTool.idOf(hashUserId);
        Optional<AppUser> optionalAppUser = appUserRepo.findById(userId);
        if (optionalAppUser.isPresent()) {
            AppUser appUser = optionalAppUser.get();
            appUser.setIsActive(true);
            appUserRepo.save(appUser);
            return true;
        }
        return false;
    }
}
