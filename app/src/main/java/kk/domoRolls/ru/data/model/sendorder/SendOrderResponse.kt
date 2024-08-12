package kk.domoRolls.ru.data.model.sendorder

data class SendOrderResponse(
    val correlationId: String,
    val orderInfo: SendOrderInfoResponse
)
data class SendOrderInfoResponse(
    val id: String,
    val posId: String,
    val externalNumber: String?,
    val organizationId: String,
    val timestamp: Long,
    val creationStatus: String,
    val errorInfo: String?,
)