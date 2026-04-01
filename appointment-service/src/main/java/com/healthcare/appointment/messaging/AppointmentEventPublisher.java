package com.healthcare.appointment.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange}")
    private String exchange;

    @Value("${rabbit.routing-key}")
    private String routingKey;

    public void publishAppointmentEvent(AppointmentEvent event) {
        log.info("Publishing appointment event for patient: {}", event.patientName());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Event published successfully to exchange: {}", exchange);
    }
}
