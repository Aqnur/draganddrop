package kz.draganddrop.utils

import android.content.res.Resources
import android.graphics.Canvas
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragItemTouchHelperCallback(
    private val targetRecyclerView: RecyclerView,
    private val nestedScrollView: NestedScrollView
) : ItemTouchHelper.Callback() {

    private var draggedFromRecyclerView: RecyclerView? = null
    private val scrollThreshold = 50 // Set a threshold for scroll trigger

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0
        draggedFromRecyclerView = recyclerView
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true // Allow items to move
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // No swipe action needed
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 1.0f
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1f
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
            scrollRecyclerViewWhenNeeded(viewHolder)
        }
    }

    private fun scrollRecyclerViewWhenNeeded(viewHolder: RecyclerView.ViewHolder) {
        val itemView = viewHolder.itemView
        val location = IntArray(2)
        itemView.getLocationOnScreen(location)

        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        // Check if dragging near top of screen
        if (location[1] < scrollThreshold) {
            nestedScrollView.smoothScrollBy(0, -20)
        }

        // Check if dragging near bottom of screen
        if (location[1] + itemView.height > screenHeight - scrollThreshold) {
            nestedScrollView.smoothScrollBy(0, 20)
        }
    }
}