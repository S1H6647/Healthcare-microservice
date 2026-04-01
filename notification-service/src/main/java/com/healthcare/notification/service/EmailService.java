package com.healthcare.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    // ─────────────────────────────────────────────
    // Core sender
    // ─────────────────────────────────────────────

    public void sendEmail(String to, String subject, String body) {
        try {
            log.info("Sending email to: {}, subject: {}", to, subject);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }

    // ─────────────────────────────────────────────
    // Shared HTML builder
    // ─────────────────────────────────────────────

    private String buildTemplate(String headerColor, String headerTitle,
                                 String headerEmoji, String bodyHtml) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f4f4f4;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center" style="padding:40px 0;">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background-color:#ffffff; border-radius:8px;
                                      box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                
                          <!-- Header -->
                          <tr>
                            <td style="background-color:%s; padding:32px;
                                       border-radius:8px 8px 0 0; text-align:center;">
                              <div style="font-size:40px; margin-bottom:12px;">%s</div>
                              <h1 style="color:#ffffff; margin:0; font-size:22px;">%s</h1>
                            </td>
                          </tr>
                
                          <!-- Body -->
                          <tr>
                            <td style="padding:32px;">
                              %s
                              <hr style="border:none; border-top:1px solid #e5e7eb; margin:24px 0;">
                              <p style="color:#6b7280; font-size:13px; margin:0;">
                                If you have any questions, feel free to contact our support team.
                              </p>
                            </td>
                          </tr>
                
                          <!-- Footer -->
                          <tr>
                            <td style="background-color:#f9fafb; padding:20px;
                                       border-radius:0 0 8px 8px; text-align:center;">
                              <p style="color:#9ca3af; font-size:12px; margin:0;">
                                © 2026 Healthcare Microservices · This is an automated message.
                              </p>
                            </td>
                          </tr>
                
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(headerColor, headerEmoji, headerTitle, bodyHtml);
    }

    private String row(String text) {
        return "<p style=\"color:#374151; font-size:15px; margin:0 0 14px;\">%s</p>"
                .formatted(text);
    }

    private String highlight(String text, String color) {
        return "<strong style=\"color:%s;\">%s</strong>".formatted(color, text);
    }

    // ─────────────────────────────────────────────
    // 1. Appointment — Pending
    // ─────────────────────────────────────────────

    public void sendAppointmentPending(String to, String patientName, String doctorName,
                                       String specialization, String appointmentDate,
                                       String startTime, String endTime) {
        String subject = "Appointment Booked — Awaiting Confirmation";
        String body = buildTemplate(
                "#F59E0B", "Appointment Pending", "⏳",
                row("Dear " + highlight(patientName, "#92400E") + ",") +
                        row("Your appointment has been booked successfully and is currently " +
                                highlight("awaiting confirmation", "#F59E0B") +
                                " from the doctor's office.") +
                        row("<strong>Doctor:</strong> " + doctorName) +
                        row("<strong>Specialization:</strong> " + specialization) +
                        row("<strong>Date:</strong> " + appointmentDate) +
                        row("<strong>Time:</strong> " + startTime + " – " + endTime) +
                        row("You will receive another notification once it is confirmed.")
        );
        sendEmail(to, subject, body);
    }

    // ─────────────────────────────────────────────
    // 2. Appointment — Confirmed
    // ─────────────────────────────────────────────

    public void sendAppointmentConfirmed(String to, String patientName, String doctorName,
                                         String specialization, String appointmentDate,
                                         String startTime, String endTime) {
        String subject = "Appointment Confirmed";
        String body = buildTemplate(
                "#10B981", "Appointment Confirmed", "✅",
                row("Dear " + highlight(patientName, "#064E3B") + ",") +
                        row("Great news! Your appointment has been " +
                                highlight("confirmed", "#10B981") + ".") +
                        row("<strong>Doctor:</strong> " + doctorName) +
                        row("<strong>Specialization:</strong> " + specialization) +
                        row("<strong>Date:</strong> " + appointmentDate) +
                        row("<strong>Time:</strong> " + startTime + " – " + endTime) +
                        row("Please arrive 10 minutes before your scheduled time and bring " +
                                "any relevant medical documents.")
        );
        sendEmail(to, subject, body);
    }

    // ─────────────────────────────────────────────
    // 3. Appointment — Cancelled
    // ─────────────────────────────────────────────

    public void sendAppointmentCancelled(String to, String patientName, String doctorName,
                                         String specialization, String appointmentDate,
                                         String startTime, String endTime) {
        String subject = "Appointment Cancelled";
        String body = buildTemplate(
                "#EF4444", "Appointment Cancelled", "❌",
                row("Dear " + highlight(patientName, "#991B1B") + ",") +
                        row("We regret to inform you that your appointment has been " +
                                highlight("cancelled", "#EF4444") + ".") +
                        row("<strong>Doctor:</strong> <del>" + doctorName + "</del>") +
                        row("<strong>Specialization:</strong> " + specialization) +
                        row("<strong>Date:</strong> <del>" + appointmentDate + "</del>") +
                        row("<strong>Time:</strong> <del>" + startTime + " – " + endTime + "</del>") +
                        row("If this was unexpected, please contact the clinic or book a " +
                                "new appointment at your convenience.")
        );
        sendEmail(to, subject, body);
    }

    // ─────────────────────────────────────────────
    // 4. Appointment — Completed
    // ─────────────────────────────────────────────

    public void sendAppointmentCompleted(String to, String patientName, String doctorName,
                                         String specialization, String appointmentDate,
                                         String startTime, String endTime) {
        String subject = "Appointment Completed";
        String body = buildTemplate(
                "#3B82F6", "Appointment Completed", "🏆",
                row("Dear " + highlight(patientName, "#1E3A8A") + ",") +
                        row("Your appointment has been marked as " +
                                highlight("completed", "#3B82F6") + ".") +
                        row("<strong>Doctor:</strong> " + doctorName) +
                        row("<strong>Specialization:</strong> " + specialization) +
                        row("<strong>Date:</strong> " + appointmentDate) +
                        row("<strong>Time:</strong> " + startTime + " – " + endTime) +
                        row("We hope your visit was helpful. If you have any follow-up " +
                                "concerns, don't hesitate to schedule another appointment.") +
                        row("Thank you for choosing our healthcare services! 🙏")
        );
        sendEmail(to, subject, body);
    }
}
