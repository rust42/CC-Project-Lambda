package blogs


data class ApiResponse(val status: Int, val message: String) {
    companion object {
        fun successful(message: String): ApiResponse {
            return ApiResponse(200, message)
        }

        fun notFound404(message: String): ApiResponse {
            return ApiResponse(status = 404, message)
        }
    }
}
