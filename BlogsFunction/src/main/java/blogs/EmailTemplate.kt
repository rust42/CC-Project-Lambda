package blogs

import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str

class EmailTemplate {
    companion object {
        fun contactConfirmation(name: String): String {
            return """<!DOCTYPE html>
<html>
<head>
    <title>Contact Request Acknowledgement</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333333;
            margin: 0;
            padding: 0;
        }
        .container {
            background-color: #ffffff;
            width: 100%%;
            max-width: 600px;
            margin: 20px auto;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .header {
            font-size: 24px;
            color: #007bff;
            text-align: center;
            margin-bottom: 20px;
        }
        .content {
            font-size: 16px;
            line-height: 1.6;
        }
        .footer {
            font-size: 14px;
            text-align: center;
            margin-top: 30px;
            color: #666666;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            Contact Request Acknowledgement
        </div>
        <div class="content">
            Hello ${name},

            <p>Thank you for reaching out to me through my blog. I appreciate you taking the time to contact me.</p>

            <p>I've received your message and will make sure to get back to you as soon as possible. In the meantime, if you have any more information to add, please feel free to reply to this email.</p>

            <p>Looking forward to our conversation!</p>

            <p>Best regards,<br>
            Sandeep</p>
        </div>
        <div class="footer">
            This is an automated response. Please do not reply directly to this email.
        </div>
    </div>
</body>
</html>"""
        }

        fun subscriptionConfirmation(identifier: String): String {
            return """
        <!DOCTYPE html>
        <html>
        <head>
        <title>Subscription Confirmation</title>
        <style type="text/css">
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333333; }
            .container { width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; }
            .header { font-size: 24px; text-align: center; padding: 20px 0; }
            .content { font-size: 16px; line-height: 1.6; }
            .footer { text-align: center; padding: 20px 0; font-size: 12px; color: #777777; }
            .button { display: inline-block; padding: 10px 20px; background-color: #007bff; color: #ffffff !important; text-decoration: none; border-radius: 5px; }
        </style>
        </head>
        <body>
        <div class="container">
        <div class="header">
        Subscription Confirmation
        </div>
        <div class="content">
        <p>Hello,</p>
        <p>Thank you for subscribing to our newsletter. Please confirm your subscription by clicking the link below:</p>
        <p style="text-align: center;">
        <a href="https://blog.k6sandeep.com/subscription/${identifier}" class="button">Confirm Subscription</a>
        </p>
        <p>If you did not request this subscription, no further action is required.</p>
        </div>
        <div class="footer">
        &copy; 2024 Your Company Name. All rights reserved.
        </div>
        </div>
        </body>
        </html>
        """
        }
    }
}
