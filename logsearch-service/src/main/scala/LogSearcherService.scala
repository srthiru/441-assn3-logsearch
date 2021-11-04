import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.{APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent}
import com.google.gson.JsonParser


object LogSearcherService extends RequestHandler[APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent]{

  override def handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent = {

    val responseEvent = new APIGatewayProxyResponseEvent
    val requestVal = input.toString
    val body = input.getBody

    responseEvent.setBody("This is the requested interval" + body.getClass)

//    val requestObject = JsonParser.parseString(requestVal).getAsJsonObject()
//
//    if(requestObject.get("requestMessage") != null){
//      responseEvent.setBody("This is the requested interval")
//    }
//    else{
//      responseEvent.setBody("Received empty request!!")
//    }

    val lambdaLogger = context.getLogger()
    lambdaLogger.log("Received request" + input.toString + "Body: " + requestVal)

    responseEvent
  }
}