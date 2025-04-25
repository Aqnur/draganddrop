package kz.draganddrop

import android.content.ClipData
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.draganddrop.data.Card
import kz.draganddrop.data.CombinedItem
import kz.draganddrop.databinding.AdapterCardBinding
import kz.draganddrop.utils.CustomDragShadowBuilder
import kz.draganddrop.utils.RecyclerViewItemClickCallback

class AdapterCard(
    private val recyclerViewItemClickCallback: RecyclerViewItemClickCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CombinedItem>() {
        override fun areItemsTheSame(oldItem: CombinedItem, newItem: CombinedItem): Boolean {
            return when {
                oldItem is CombinedItem.CardItem && newItem is CombinedItem.CardItem ->
                    oldItem.card.id == newItem.card.id
                oldItem is CombinedItem.DepositItem && newItem is CombinedItem.DepositItem ->
                    oldItem.deposit.id == newItem.deposit.id
                oldItem is CombinedItem.Header && newItem is CombinedItem.Header ->
                    oldItem.title == newItem.title
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: CombinedItem, newItem: CombinedItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<CombinedItem>) {
        differ.submitList(list)
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CARD = 1
        private const val VIEW_TYPE_DEPOSIT = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(view)
            }

            VIEW_TYPE_CARD -> {
                val binding = AdapterCardBinding.inflate(inflater, parent, false)
                CardViewHolder(binding)
            }

            VIEW_TYPE_DEPOSIT -> {
                val binding = AdapterCardBinding.inflate(inflater, parent, false)
                CardViewHolder(binding)
            }


            else -> {
                throw IllegalStateException("Incorrect ViewType found")
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            (itemView as TextView).text = title
        }
    }

    inner class CardViewHolder(private val binding: AdapterCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun initContent(data: Card) {
            binding.name.text = data.name
            binding.amount.text = data.amount
            if (data.isBeingDragged) {
                binding.root.visibility = View.INVISIBLE
            } else {
                binding.root.visibility = View.VISIBLE
            }

            binding.root.setOnLongClickListener {
                // Only allow dragging Cards or Deposits, skip headers
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnLongClickListener false

                val item = differ.currentList[position]
                val dragData: ClipData
                val localState: Any

                when (item) {
                    is CombinedItem.CardItem -> {
                        item.card.isBeingDragged = true
                        dragData = ClipData.newPlainText("card", "card")
                        localState = item
                    }

                    is CombinedItem.DepositItem -> {
                        item.deposit.isBeingDragged = true
                        dragData = ClipData.newPlainText("deposit", "deposit")
                        localState = item
                    }

                    else -> return@setOnLongClickListener false // skip headers
                }

                val shadowBuilder = CustomDragShadowBuilder(binding.root)
                binding.root.visibility = View.INVISIBLE

                binding.root.startDragAndDrop(dragData, shadowBuilder, localState, 0)
                true
            }

            binding.root.setOnDragListener { view, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        val label = event.clipDescription?.label
                        val isValidTarget = when (differ.currentList[adapterPosition]) {
                            is CombinedItem.CardItem, is CombinedItem.DepositItem -> true
                            else -> false
                        }
                        isValidTarget && (label == "card" || label == "deposit")
                    }

                    DragEvent.ACTION_DRAG_ENTERED -> {
                        view.alpha = 0.7f
                        true
                    }

                    DragEvent.ACTION_DRAG_EXITED -> {
                        view.alpha = 1f
                        true
                    }

                    DragEvent.ACTION_DROP -> {
                        view.alpha = 1f

                        val draggedItem = event.localState as CombinedItem
                        val targetItem = differ.currentList.getOrNull(adapterPosition) as CombinedItem

                        recyclerViewItemClickCallback.onDragAndDrop(draggedItem, targetItem)
                        true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        view.alpha = 1f

                        val draggedItem = event.localState
                        val index = differ.currentList.indexOf(draggedItem)
                        if (index != -1) {
                            when (draggedItem) {
                                is CombinedItem.CardItem -> draggedItem.card.isBeingDragged = false
                                is CombinedItem.DepositItem -> draggedItem.deposit.isBeingDragged = false
                            }
                            notifyItemChanged(index)
                        }

                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = differ.currentList[position]) {
            is CombinedItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is CombinedItem.CardItem -> (holder as CardViewHolder).initContent(item.card)
            is CombinedItem.DepositItem -> (holder as CardViewHolder).initContent(item.deposit)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (differ.currentList[position]) {
            is CombinedItem.Header -> VIEW_TYPE_HEADER
            is CombinedItem.CardItem -> VIEW_TYPE_CARD
            is CombinedItem.DepositItem -> VIEW_TYPE_DEPOSIT
            else -> throw IllegalStateException("Incorrect ViewType found")
        }

}