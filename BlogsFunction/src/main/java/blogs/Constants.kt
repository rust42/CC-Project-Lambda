package blogs
class Constants() {
    companion object {
        private const val SQS_QUEUE_URL_ENV_NAME = "SQS_QUEUE_URL_ENV_NAME"
        private const val EMAIL_SENDER = "SES_EMAIL_SENDER_EMAIL_ADDRESS"
        private const val EMAIL_BCC_ADDRESS = "SES_EMAIL_BCC_ADDRESS"

        val sqsUrl: String get() = System.getenv(SQS_QUEUE_URL_ENV_NAME)
        val emailAdmin: String get() = System.getenv(EMAIL_SENDER)
        val bccAddress: String get() = System.getenv(EMAIL_BCC_ADDRESS)
    }
}
