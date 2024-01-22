package blogs

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent
class ContactDynamoDBStream : RequestHandler<DynamodbEvent, String> {
    private val emailSender = EmailSender()
    override fun handleRequest(ddbEvent: DynamodbEvent, context: Context): String {
        val records = ddbEvent.records
        for (record in records) {
            if (record.eventName != "INSERT") {
                continue
            }
            val newRecords = record.dynamodb.newImage
            val nameAttribute = newRecords["name"]
            val emailAttribute = newRecords["email"]
            if (nameAttribute != null && emailAttribute != null) {
                emailSender.sendContactConfirmationEmail(name = nameAttribute.s, toEmail = emailAttribute.s)
            } else {
                context.logger.log("Name or email attribute missing")
            }
        }
        return "Successfully processed request"
    }
}

