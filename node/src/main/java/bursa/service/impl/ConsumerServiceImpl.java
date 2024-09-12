package bursa.service.impl;

import bursa.service.ConsumerService;
import bursa.service.MainService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

}
