package bursa.service;

import bursa.entities.AppUser;

public interface AppUserService {
    String registerUser(AppUser user);
    String setEmail(AppUser user, String email);
}
