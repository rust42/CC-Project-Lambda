package blogs

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.lang.RuntimeException

class SubscriptionsSQSIngester: RequestHandler<SQSEvent, String> {
    private val mapper = ObjectMapper().registerKotlinModule()

    private val emailSender = EmailSender()
    override fun handleRequest(event: SQSEvent?, context: Context?): String {
        val records = event?.records

        if (records == null || records.size == 0) {
            throw RuntimeException("SQSEvent triggered with empty records")
        }
        context?.logger?.log("Processing ${records.size} records")
        for (record in records) {
            val body = record.body

            if (body == null) {
                context?.logger?.log("body is null")
                continue
            }
            val contact = mapper.readValue<SubscriptionSQSBody>(body)
            context?.logger?.log("Found record: ${contact.email}: ${contact.identifier}")
            context?.logger?.log("Sending email to: ${contact.email}")
            sendSubscriptionVerificationEmail(contact.identifier, contact.email,)
        }
        return "Processed ${event.records?.size ?: 0}"
    }

    private fun sendSubscriptionVerificationEmail(identifier: String, email: String) {
        emailSender.sendSubscriptionEmail(identifier, email)
    }
}