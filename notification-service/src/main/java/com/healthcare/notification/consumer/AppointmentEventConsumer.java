package com.healthcare.notification.consumer;

import com.healthcare.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentEventConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "appointment.queue")
    public void consume(Map<String, String> event) {
        String status = event.get("status");
        log.info("Received event: status={}, patient={}", status, event.get("patientName"));

        String to = event.get("patientEmail");
        String patientName = event.get("patientName");
        String doctorName = event.get("doctorName");
        String specialization = event.get("specialization");
        String appointmentDate = event.get("appointmentDate");
        String startTime = event.get("startTime");
        String endTime = event.get("endTime");

        switch (status) {
            case "PENDING" -> emailService.sendAppointmentPending(to, patientName, doctorName, specialization, appointmentDate, startTime, endTime);
            case "CONFIRMED" ->
                    emailService.sendAppointmentConfirmed(to, patientName, doctorName, specialization, appointmentDate, startTime, endTime);
            case "CANCELLED" ->
                    emailService.sendAppointmentCancelled(to, patientName, doctorName, specialization, appointmentDate, startTime, endTime);
            case "COMPLETED" ->
                    emailService.sendAppointmentCompleted(to, patientName, doctorName, specialization, appointmentDate, startTime, endTime);
        }
    }
}
