package kz.draganddrop.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.view.View

class CustomDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    private val scaleFactor = 1.2f
    private val shadowBitmap: Bitmap = createScaledBitmap(view, scaleFactor)

    override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
        outShadowSize.set(shadowBitmap.width, shadowBitmap.height)
        outShadowTouchPoint.set(shadowBitmap.width / 2, shadowBitmap.height / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {
        val paint = Paint()
        paint.alpha = 255
        canvas.drawBitmap(shadowBitmap, 0f, 0f, paint)
    }

    private fun createScaledBitmap(view: View, scale: Float): Bitmap {
        val original = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(original)
        view.draw(canvas)

        val scaledWidth = (view.width * scale).toInt()
        val scaledHeight = (view.height * scale).toInt()

        return Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true)
    }


}