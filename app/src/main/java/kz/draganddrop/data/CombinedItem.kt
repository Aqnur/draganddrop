package kz.draganddrop.data

sealed class CombinedItem {
    data class Header(val title: String) : CombinedItem()
    data class CardItem(val card: Card) : CombinedItem()
    data class DepositItem(val deposit: Card) : CombinedItem()
}