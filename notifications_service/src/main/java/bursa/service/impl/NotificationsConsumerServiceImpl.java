package bursa.service.impl;

import bursa.service.NotificationsConsumerService;
import bursa.service.MainService;
import org.springframework.stereotype.Service;

@Service
public class NotificationsConsumerServiceImpl implements NotificationsConsumerService {
    private final MainService mainService;

    public NotificationsConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

}
