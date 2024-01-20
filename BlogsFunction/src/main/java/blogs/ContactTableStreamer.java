package blogs;

import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

class ContactMeEmailSender {

    private SesClient client;
    final String sender = "admin@k6sandeep.com";
    final String subject = "Thanks for reaching out";
    final String htmlBodyFormat = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Contact Request Acknowledgement</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            background-color: #f4f4f4;\n" +
            "            color: #333333;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "        .container {\n" +
            "            background-color: #ffffff;\n" +
            "            width: 100%%;\n" + // Escaped %
            "            max-width: 600px;\n" +
            "            margin: 20px auto;\n" +
            "            padding: 20px;\n" +
            "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
            "        }\n" +
            "        .header {\n" +
            "            font-size: 24px;\n" +
            "            color: #007bff;\n" +
            "            text-align: center;\n" +
            "            margin-bottom: 20px;\n" +
            "        }\n" +
            "        .content {\n" +
            "            font-size: 16px;\n" +
            "            line-height: 1.6;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            font-size: 14px;\n" +
            "            text-align: center;\n" +
            "            margin-top: 30px;\n" +
            "            color: #666666;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"header\">\n" +
            "            Contact Request Acknowledgement\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            Hello %s,\n" + // Unescaped % for the placeholder
            "\n" +
            "            <p>Thank you for reaching out to me through my blog. I appreciate you taking the time to contact me.</p>\n" +
            "\n" +
            "            <p>I've received your message and will make sure to get back to you as soon as possible. In the meantime, if you have any more information to add, please feel free to reply to this email.</p>\n" +
            "\n" +
            "            <p>Looking forward to our conversation!</p>\n" +
            "\n" +
            "            <p>Best regards,<br>\n" +
            "            Sandeep</p>\n" +
            "        </div>\n" +
            "        <div class=\"footer\">\n" +
            "            This is an automated response. Please do not reply directly to this email.\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";


    void sendEmail(String name, String toEmail) {
        Destination destination = Destination.builder()
                .toAddresses(toEmail)
                .bccAddresses("sndpkrl007@gmail.com")
                .build();
        Content content = Content.builder()
                .data(String.format(htmlBodyFormat, name))
                .build();
        Content sub = Content.builder()
                .data(subject)
                .build();
        Body body = Body.builder()
                .html(content)
                .build();
        Message msg = Message.builder()
                .subject(sub)
                .body(body)
                .build();
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .message(msg)
                .source(sender)
                .build();

        try {
            System.out.println("Attempting to send an email");
            SesClient client = SesClient.builder()
                        .region(Region.US_EAST_1)
                        .build();
            client.sendEmail(emailRequest);
            client.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}


public class ContactTableStreamer implements RequestHandler<DynamodbEvent, String> {

    final private ContactMeEmailSender emailSender = new ContactMeEmailSender();
    public String handleRequest(DynamodbEvent ddbEvent, final Context context) {
          List<DynamodbEvent.DynamodbStreamRecord> records = ddbEvent.getRecords();
          for (DynamodbEvent.DynamodbStreamRecord record: records) {
              if (!record.getEventName().equals("INSERT")) {
                continue;
              }
              var newRecords = record.getDynamodb().getNewImage();
              AttributeValue nameAttribute = newRecords.get("name");
              AttributeValue emailAttribute = newRecords.get("email");
              if (nameAttribute != null && emailAttribute != null) {
                  emailSender.sendEmail(nameAttribute.getS(), emailAttribute.getS());
              } else {
                  context.getLogger().log("Name or email attribute missing");
              }

          }
        return "Successfully processed request";
    }
}
