package bursa.dispatcher.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static bursa.model.RabbitQueue.*;

@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private Queue createQueueWithDLQ(String queueName) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "deadLetterExchange");
        args.put("x-dead-letter-routing-key", queueName + ".dlq");
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public Queue textMessageQueue() {
        return createQueueWithDLQ(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue videoMessageQueue() {
        return createQueueWithDLQ(VIDEO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue docMessageQueue() {
        return createQueueWithDLQ(DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Queue audioMessageQueue() {
        return createQueueWithDLQ(AUDIO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue photoMessageQueue() {
        return createQueueWithDLQ(PHOTO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageQueue() {
        return createQueueWithDLQ(ANSWER_MESSAGE);
    }

    @Bean
    public Queue notificationMessageQueue() {
        return createQueueWithDLQ(NOTIFICATION_MESSAGE_UPDATE);
    }

    @Bean
    public Queue callbackQueryQueue() {
        return createQueueWithDLQ(CALLBACK_QUERY);
    }

    @Bean
    public Queue editMessageQueue() {
        return createQueueWithDLQ(EDIT_MESSAGE);
    }

    @Bean
    public Queue deleteMessageQueue() {
        return createQueueWithDLQ(DELETE_MESSAGE);
    }

    @Bean
    public Queue notificationEditTextMessageQueue() {
        return createQueueWithDLQ(NOTIFICATION_EDIT_TEXT_MESSAGE);
    }

    @Bean
    public Queue notificationEditTimeMessageQueue() {
        return createQueueWithDLQ(NOTIFICATION_EDIT_TIME_MESSAGE);
    }

    @Bean
    public Queue dicsCallbackQueryQueue() {
        return createQueueWithDLQ(DISC_CALLBACK_QUERY);
    }

    private Queue createDLQ(String queueName) {
        return new Queue(queueName + ".dlq");
    }

    @Bean
    public Queue photoMessageDLQ() {
        return createDLQ(PHOTO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue textMessageDLQ() {
        return createDLQ(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue videoMessageDLQ() {
        return createDLQ(VIDEO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue docMessageDLQ() {
        return createDLQ(DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Queue audioMessageDLQ() {
        return createDLQ(AUDIO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageDLQ() {
        return createDLQ(ANSWER_MESSAGE);
    }

    @Bean
    public Queue notificationMessageDLQ() {
        return createDLQ(NOTIFICATION_MESSAGE_UPDATE);
    }

    @Bean
    public Queue callbackQueryDLQ() {
        return createDLQ(CALLBACK_QUERY);
    }

    @Bean
    public Queue editMessageDLQ() {
        return createDLQ(EDIT_MESSAGE);
    }

    @Bean
    public Queue deleteMessageDLQ() {
        return createDLQ(DELETE_MESSAGE);
    }

    @Bean
    public Queue notificationEditTextMessageDLQ() {
        return createDLQ(NOTIFICATION_EDIT_TEXT_MESSAGE);
    }

    @Bean
    public Queue notificationEditTimeMessageDLQ() {
        return createDLQ(NOTIFICATION_EDIT_TIME_MESSAGE);
    }

    @Bean
    public Queue discCallbackQueryDLQ() {
        return createDLQ(DISC_CALLBACK_QUERY);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("deadLetterExchange");
    }

    private Binding createDLQBinding(String queueName) {
        return BindingBuilder.bind(createDLQ(queueName))
                .to(deadLetterExchange())
                .with(queueName + ".dlq");
    }

    @Bean
    public Binding textMessageDLQBinding() {
        return createDLQBinding(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Binding videoMessageDLQBinding() {
        return createDLQBinding(VIDEO_MESSAGE_UPDATE);
    }

    @Bean
    public Binding docMessageDLQBinding() {
        return createDLQBinding(DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Binding audioMessageDLQBinding() {
        return createDLQBinding(AUDIO_MESSAGE_UPDATE);
    }

    @Bean
    public Binding answerMessageDLQBinding() {
        return createDLQBinding(ANSWER_MESSAGE);
    }

    @Bean
    public Binding notificationMessageDLQBinding() {
        return createDLQBinding(NOTIFICATION_MESSAGE_UPDATE);
    }

    @Bean
    public Binding callbackQueryDLQBinding() {
        return createDLQBinding(CALLBACK_QUERY);
    }

    @Bean
    public Binding editMessageDLQBinding() {
        return createDLQBinding(EDIT_MESSAGE);
    }

    @Bean
    public Binding deleteMessageDLQBinding() {
        return createDLQBinding(DELETE_MESSAGE);
    }

    @Bean
    public Binding notificationEditTextMessageDLQBinding() {
        return createDLQBinding(NOTIFICATION_EDIT_TEXT_MESSAGE);
    }

    @Bean
    public Binding notificationEditTimeMessageDLQBinding() {
        return createDLQBinding(NOTIFICATION_EDIT_TIME_MESSAGE);
    }

    @Bean
    public Binding discCallbackQueryDLQBinding() {
        return createDLQBinding(DISC_CALLBACK_QUERY);
    }

    @Bean
    public Binding photoMessageDLQBinding() {
        return createDLQBinding(PHOTO_MESSAGE_UPDATE);
    }

}
