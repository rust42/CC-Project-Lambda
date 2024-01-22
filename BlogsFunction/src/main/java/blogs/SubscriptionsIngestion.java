package blogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;


public class Subscriptions implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper mapper = new ObjectMapper();
    final DynamoDbClient ddb = DynamoDbClient.builder().region(Region.US_EAST_1).build();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Credentials", "true");

        try {
            Map requestBody = mapper.readValue(input.getBody(), Map.class);
            String name = (String) requestBody.get("name");
            String email = (String) requestBody.get("email");
            String message = (String) requestBody.get("name");

            UUID uuid = UUID.randomUUID();

            Map<String, AttributeValue> item  = new HashMap<>();
            item.put("email", AttributeValue.builder().s(email).build());
            item.put("UUID", AttributeValue.builder().s(uuid.toString()).build());
            item.put("isVerified", AttributeValue.builder().bool(false).build());
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                    .withHeaders(headers);
            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName("Subscriptions").item(item).build();

            PutItemResponse putItemResponse = ddb.putItem(putItemRequest);
            System.out.println(putItemResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            return response
                    .withStatusCode(200)
                    .withBody("Hello world from Blogs App");
        } catch (Exception e) {
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
