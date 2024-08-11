package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetOrdersRequest(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("organizationIds")
    val organizationIds: List<String> = listOf("03a1584e-1c80-4071-829d-997688b68cba"),
)

data class GetOrderByIdRequest(
    @SerializedName("orderIds")
    val orderIds: List<String>,
    @SerializedName("organizationId")
    val organizationId: String = "03a1584e-1c80-4071-829d-997688b68cba",
)