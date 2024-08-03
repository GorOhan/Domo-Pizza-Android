package kk.domoRolls.ru.domain.model.map

data class Polygon(
    val cost: Int,
    val color: String,
    val stroke: String,
    val coordinates: List<List<Double>>
)
