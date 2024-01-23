package blogs

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.util.*
import kotlin.collections.mapOf;


data class SubscriptionsRequest(val email: String)
class Subscriptions: RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val mapper = ObjectMapper().registerKotlinModule()
    private val dynamoDb = DynamoDbClient.builder().region(Region.US_EAST_1).build()

    override fun handleRequest(event: APIGatewayProxyRequestEvent?, contact: Context?): APIGatewayProxyResponseEvent {
        val response = APIGatewayProxyResponseEvent().addCorsHeaders()
        event?.body?.let {
            val request = mapper.readValue<SubscriptionsRequest>(it)
            store(request.email)
            return response.withBody("Subscriptions added")
        }
        return response.withStatusCode(500)
    }

    private fun store(email: String) {
        val identifier = UUID.randomUUID().toString()

        val attributes = mutableMapOf<String, AttributeValue>()
        attributes["email"] = AttributeValue.builder().s(email).build()
        attributes["identifier"] = AttributeValue.builder().s(identifier).build()
        attributes["verified"] = AttributeValue.builder().bool(false).build()

        val putItemRequest = PutItemRequest.builder().tableName("Subscriptions")
                .item(attributes)
                .build()

        dynamoDb.putItem(putItemRequest)
        sendSQSMessage(email, identifier)
    }

    private fun sendSQSMessage(email: String, identifier: String) {
        val client = SqsClient.builder().region(Region.US_EAST_1).build()

        val message = mapOf(Pair("email", email), Pair("identifier", identifier))
        val messageBody = mapper.writeValueAsString(message)

        val messageRequest = SendMessageRequest.builder()
            .queueUrl(Constants.sqsUrl)
            .messageDeduplicationId(identifier)
            .messageBody(messageBody)
            .build()
        client.sendMessage(messageRequest)
        client.close()
    }
}