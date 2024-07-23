package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetStreetsResponse(
    @SerializedName("correlationId")
    val correlationId: String?,
    @SerializedName("streets")
    val streets: List<Street>
)

data class Street(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("externalRevision")
    val externalRevision: Int?,
    @SerializedName("classifierId")
    val classifierId: String?,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?,

)
