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
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse
import java.util.UUID

data class ContactRequest(val email: String, val name: String, val message: String);
class Contact: RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val mapper = ObjectMapper().registerKotlinModule()
    private val dynamoDb = DynamoDbClient.builder().region(Region.US_EAST_1).build()
    override fun handleRequest(event: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        try {
            event?.body?.let {
                val contact = mapper.readValue<ContactRequest>(it)
                val attributes = createAttributes(contact)
                store(attributes)
                return APIGatewayProxyResponseEvent().addBody(ApiResponse.successful("Successfully added contact request"))
            }
        } catch (ex: Exception) {
            return APIGatewayProxyResponseEvent().addBody(ApiResponse.notFound404(ex.message?: "Invalid request"))
        }
        return APIGatewayProxyResponseEvent().addBody(ApiResponse.notFound404("Invalid request body"))
    }

    private fun createAttributes(contact: ContactRequest): Map<String, AttributeValue> {
        val map = mutableMapOf<String, AttributeValue>()
        map["ID"] = AttributeValue.builder().s(UUID.randomUUID().toString()).build()
        map["name"] = AttributeValue.builder().s(contact.name).build()
        map["email"] = AttributeValue.builder().s(contact.email).build()
        map["message"] = AttributeValue.builder().s(contact.message).build()
        return map
    }

    private fun store(attributes: Map<String, AttributeValue>): PutItemResponse {
        val putItemRequest = PutItemRequest.builder().tableName("Contact")
                .item(attributes)
                .build()
        return dynamoDb.putItem(putItemRequest)
    }
}

