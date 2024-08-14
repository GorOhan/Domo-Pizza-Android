package kk.domoRolls.ru.presentation.cart

enum class PromoCodeUseState(val dialogMessage: String) {
    SUCCESS("Промокод успешно применен"),
    ALREADY_USED("Промокод уже использован"),
    WRONG("ошибка неправильный промокод"),
    HAS_MIN_PRICE("Для использования этого промокода цена корзины должна быть больше"),
    NOTHING("")
}