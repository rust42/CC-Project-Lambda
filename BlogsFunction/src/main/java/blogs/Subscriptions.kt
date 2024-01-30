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
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.lang.Exception
import java.util.*


data class SubscriptionSQSBody(val identifier: String, val email: String)
data class SubscriptionVerificationBody(val identifier: String)

data class SubscriptionsRequest(val email: String)
class Subscriptions: RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val mapper = ObjectMapper().registerKotlinModule()
    private val dynamoDb = DynamoDbClient.builder().region(Region.US_EAST_1).build()

    override fun handleRequest(event: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val requestPath = event?.path

        context?.logger?.log("Request path: $requestPath")

        try {
            if (requestPath == "/subscription/verify") {
                return handleVerificationRequest(event, context)
            }
            return handleSubscriptionsRequest(event, context)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return APIGatewayProxyResponseEvent().addBody(ApiResponse.notFound404("Could not parse the request"))
    }

    private fun handleVerificationRequest(event: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val body = event?.body ?: throw Exception("Invalid body received")

        val request = mapper.readValue<SubscriptionVerificationBody>(body)
        return verify(request.identifier, context)
    }

    private fun handleSubscriptionsRequest(event: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val body = event?.body
        val response = APIGatewayProxyResponseEvent()

        if (body == null) {
            throw Exception("Invalid body received")
        }

        val request = mapper.readValue<SubscriptionsRequest>(body)
        store(request.email)
        return response.addBody(ApiResponse.successful("Successfully added to subscription, please verify"))
    }

    private fun verify(identifier: String, context: Context?): APIGatewayProxyResponseEvent {
        val queryRequest = QueryRequest.builder()
            .tableName("Subscriptions")
            .indexName("identifier")
            .keyConditionExpression("identifier = :v_id")
            .expressionAttributeValues(
                mapOf(":v_id" to AttributeValue.builder().s(identifier).build())
            ).build()
        val queryResult = dynamoDb.query(queryRequest)
        val item = queryResult.items().first()
            ?: return APIGatewayProxyResponseEvent().addBody(ApiResponse.notFound404("No such item found"))

        val email = item["email"]?.s()

        val updateRequest = UpdateItemRequest.builder()
            .tableName("Subscriptions")
            .key(mapOf("email" to AttributeValue.builder().s(email).build()))
            .updateExpression("SET verified = :v")
            .expressionAttributeValues(mapOf(":v" to AttributeValue.builder().bool(true).build()))
            .returnValues("ALL_NEW")
            .build()
        val response = dynamoDb.updateItem(updateRequest)
        context?.logger?.log("Updated items $response")
        return APIGatewayProxyResponseEvent()
            .addBody(ApiResponse.successful("Successfully verified"))
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

        val message = SubscriptionSQSBody(identifier = identifier, email = email)

        val messageBody = mapper.writeValueAsString(message)

        val messageRequest = SendMessageRequest.builder()
            .queueUrl(Constants.sqsUrl)
            .messageGroupId(email)
            .messageDeduplicationId(identifier)
            .messageBody(messageBody)
            .build()
        client.sendMessage(messageRequest)
        client.close()
    }
}