package blogs

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import java.lang.RuntimeException

class SubscriptionsSQSIngester: RequestHandler<SQSEvent, String> {
    private val emailSender = EmailSender()
    override fun handleRequest(event: SQSEvent?, context: Context?): String {
        event?.records?.let {
            for (record in it) {
                val attributes = record.attributes
                val email = attributes["email"]
                val identifier = attributes["identifier"]
                if (email != null && identifier != null) {
                    sendSubscriptionVerificationEmail(email, identifier)
                }
            }
            return "Processed ${event.records?.size ?: 0}"
        }
        throw  RuntimeException("SQSEvent triggered with empty records")
    }

    private fun sendSubscriptionVerificationEmail(identifier: String, email: String) {
        emailSender.sendSubscriptionEmail(identifier, email)
    }
}