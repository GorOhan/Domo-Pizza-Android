package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetMenuRequest(
    @SerializedName("externalMenuId")
    val externalMenuId: String = "9027",
    @SerializedName("organizationIds")
    val organizationIds: List<String> = listOf("03a1584e-1c80-4071-829d-997688b68cba"),
    )
