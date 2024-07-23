package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetStreetsRequest(
    @SerializedName("organizationId")
    val organizationId: String = "03a1584e-1c80-4071-829d-997688b68cba",
    @SerializedName("cityId")
    val cityId: String = "b090de0b-8550-6e17-70b2-bbba152bcbd3"
)