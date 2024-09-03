package bursa.dispatcher.configurations;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static bursa.model.RabbitQueue.*;


@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue videoMessageQueue() {
        return new Queue(VIDEO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Queue audioMessageQueue() {
        return new Queue(AUDIO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(ANSWER_MESSAGE);
    }

    @Bean
    public Queue notificationMessageQueue() {
        return new Queue(NOTIFICATION_MESSAGE_UPDATE);
    }

    @Bean
    public Queue callbackQueryQueue() {
        return new Queue(CALLBACK_QUERY);
    }

    @Bean
    public Queue editMessageQueue() {
        return new Queue(EDIT_MESSAGE);
    }

    @Bean
    public Queue deleteMessageQueue() {
        return new Queue(DELETE_MESSAGE);
    }

    @Bean
    public Queue notificationEditTextMessageQueue() {
        return new Queue(NOTIFICATION_EDIT_TEXT_MESSAGE);
    }

    @Bean
    public Queue notificationEditTimeMessageQueue() {
        return new Queue(NOTIFICATION_EDIT_TIME_MESSAGE);
    }
}
