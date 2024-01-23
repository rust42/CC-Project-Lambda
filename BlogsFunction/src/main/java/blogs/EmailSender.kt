package blogs

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*

class EmailSender {
    private fun sendEmail(subject: String, message: String, toEmail: String) {
        val sender = "admin@k6sandeep.com"
        val destination = Destination.builder()
                .toAddresses(toEmail)
                .bccAddresses("sndpkrl007@gmail.com")
                .build()
        val content = Content.builder()
                .data(message)
                .build()
        val sub = Content.builder()
                .data(subject)
                .build()
        val body = Body.builder()
                .html(content)
                .build()
        val msg = Message.builder()
                .subject(sub)
                .body(body)
                .build()
        val emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .message(msg)
                .source(sender)
                .build()
        try {
            println("Attempting to send an email")
            val client = SesClient.builder()
                    .region(Region.US_EAST_1)
                    .build()
            client.sendEmail(emailRequest)
            client.close()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun sendContactConfirmationEmail(name: String, toEmail: String) {
        sendEmail("Thanks for reaching out", EmailTemplate.contactConfirmation(name), toEmail)
    }

    fun sendSubscriptionEmail(identifier: String, toEmail: String) {
        sendEmail("Subcription confirmation", EmailTemplate.subscriptionConfirmation(identifier), toEmail)
    }
}

