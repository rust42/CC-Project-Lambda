package blogs

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

fun APIGatewayProxyResponseEvent.addCorsHeaders(): APIGatewayProxyResponseEvent {
    val headers = mutableMapOf<String, String>()
    headers["Content-Type"] = "application/json"
    headers["X-Custom-Header"] = "application/json"
    headers["Access-Control-Allow-Origin"] = "*"
    headers["Access-Control-Allow-Credentials"] = "true"
    return withHeaders(headers)
}

fun APIGatewayProxyResponseEvent.addBody(body: ApiResponse): APIGatewayProxyResponseEvent {
    val mapper = ObjectMapper().registerKotlinModule()
    return APIGatewayProxyResponseEvent().addCorsHeaders()
        .withBody(mapper.writeValueAsString(body))
        .withStatusCode(body.status)
}