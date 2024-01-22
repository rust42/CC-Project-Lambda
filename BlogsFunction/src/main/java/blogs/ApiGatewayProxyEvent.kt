package blogs

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

fun APIGatewayProxyResponseEvent.addCorsHeaders(): APIGatewayProxyResponseEvent {
    val headers = mutableMapOf<String, String>()
    headers["Content-Type"] = "application/json"
    headers["X-Custom-Header"] = "application/json"
    headers["Access-Control-Allow-Origin"] = "*"
    headers["Access-Control-Allow-Credentials"] = "true"
    return withHeaders(headers)
}