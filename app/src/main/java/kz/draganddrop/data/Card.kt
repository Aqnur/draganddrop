package kz.draganddrop.data

import androidx.annotation.Keep

@Keep
data class Card(
    val id: String,
    val type: CardTypeEnums,
    val name: String,
    val amount: String,
    var isBeingDragged: Boolean = false
)

enum class CardTypeEnums {
    CARD, DEPOSIT, LOAN
}
