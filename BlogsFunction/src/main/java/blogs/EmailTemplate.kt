package blogs

class EmailTemplate {
    companion object {
        fun contactConfirmation(name: String): String {
            val htmlBodyFormat = """<!DOCTYPE html>
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
            Hello %s,

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
            return String.format(htmlBodyFormat, name)
        }
    }
}
