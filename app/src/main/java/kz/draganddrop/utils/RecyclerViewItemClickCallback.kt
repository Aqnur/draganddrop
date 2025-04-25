package kz.draganddrop.utils

import kz.draganddrop.data.CombinedItem

interface RecyclerViewItemClickCallback {

    fun onRecyclerViewItemClick(any: Any)

    fun onDragAndDrop(fromItem: CombinedItem, toItem: CombinedItem)
}