package blogs
class Constants() {
    companion object {
        private const val SQS_QUEUE_URL_ENV_NAME = "SQS_QUEUE_URL_ENV_NAME"

        val sqsUrl: String get() = System.getenv(SQS_QUEUE_URL_ENV_NAME)
    }
}
