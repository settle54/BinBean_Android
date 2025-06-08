package com.binbean.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.binbean.admin.dto.FloorDetail
import com.binbean.admin.dto.Position
import com.binbean.domain.cafe.FloorListDto
import com.binbean.domain.cafe.PositionDto

class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        setWillNotDraw(false)  // 이게 없으면 onDraw()가 호출되지 않음
        setBackgroundColor(Color.WHITE)
    }

    private val seatList = mutableListOf<Seat>()  // 좌석 정보
    private val objectList = mutableListOf<Object>()  // 문, 창문 등

    data class Seat(val x: Float, val y: Float, val isOccupied: Boolean)
    data class Object(val x: Float, val y: Float, val type: ObjectType)

    enum class ObjectType { SEAT, DOOR, TOILET, COUNTER, WINDOW, TABLE }

    enum class Mode {
        USER, ADMIN
    }
    var mode: Mode = Mode.USER

    private val gridSize = 50f
    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 1f
    }

    private var offsetX = 0f
    private var lastX = 0f
    private var isScrolling = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(offsetX, 0f)

        drawGrid(canvas)
        drawSeats(canvas)
        drawObjects(canvas)

        canvas.restore()
    }

//    override fun dispatchDraw(canvas: Canvas) {
//        canvas.save()
//        canvas.translate(offsetX, 0f)
//        drawGrid(canvas)
//        canvas.restore()
//        super.dispatchDraw(canvas)
//    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                isScrolling = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isScrolling) {
                    val dx = event.x - lastX
                    offsetX += dx
                    lastX = event.x
                    invalidate()
                    updateObjectTranslation()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isScrolling = false
            }
        }
        return true
    }

    private fun drawGrid(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        for (x in 0..(width / gridSize).toInt()) {
            canvas.drawLine(x * gridSize, 0f, x * gridSize, height, gridPaint)
        }
        for (y in 0..(height / gridSize).toInt()) {
            canvas.drawLine(0f, y * gridSize, width, y * gridSize, gridPaint)
        }
    }

    private fun updateObjectTranslation() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.translationX = offsetX
        }
    }

    fun getOffsetX(): Float = offsetX

    fun renderFloorPlan(data: FloorListDto, currentSeats: List<PositionDto>) {
        seatList.clear()
        objectList.clear()

        data.seatPosition.forEach {
            val isOccupied = currentSeats.any { seat -> seat.x == it.x && seat.y == it.y }
            seatList.add(Seat(it.x, it.y, isOccupied))
        }
        data.doorPosition.forEach { objectList.add(Object(it.x, it.y, ObjectType.DOOR)) }
        data.counterPosition.forEach { objectList.add(Object(it.x, it.y, ObjectType.COUNTER)) }
        data.toiletPosition.forEach { objectList.add(Object(it.x, it.y, ObjectType.TOILET)) }
        data.windowPosition.forEach { objectList.add(Object(it.x, it.y, ObjectType.WINDOW)) }

        invalidate()

        Log.d("CanvasView", "좌석 수: ${data.seatPosition.size}")
    }

    private fun addObjectAt(x: Float, y: Float, drawableResId: Int, tag: String? = null) {
        val imageView = ImageView(context).apply {
            setImageResource(drawableResId)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            this.x = x * gridSize
            this.y = y * gridSize
            this.tag = tag
        }
        addView(imageView)
    }

    private fun addSeat(x: Float, y: Float, occupied: Boolean) {
        val view = ImageView(context).apply {
            setImageResource(R.drawable.obj_seat)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            this.x = x * gridSize
            this.y = y * gridSize

            if (occupied) {
                val green = ContextCompat.getColor(context, R.color.main_green)
                setColorFilter(green)
            }
        }
        addView(view)
    }

    private fun drawSeats(canvas: Canvas) {
        seatList.forEach {
            val resId = R.drawable.obj_seat
            val bitmap = getBitmapFromDrawable(resId)

            val cx = it.x * gridSize
            val cy = it.y * gridSize

            if (it.isOccupied) {
                val colored = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
                val paint = Paint()
                val canvasTemp = Canvas(colored)
                paint.colorFilter = android.graphics.PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.main_green),
                    android.graphics.PorterDuff.Mode.SRC_ATOP
                )
                canvasTemp.drawBitmap(colored, 0f, 0f, paint)
                canvas.drawBitmap(colored, cx, cy, null)
            } else {
                canvas.drawBitmap(bitmap, cx, cy, null)
            }
        }
    }

    private fun drawObjects(canvas: Canvas) {
        objectList.forEach {
            val resId = when (it.type) {
                ObjectType.DOOR -> R.drawable.obj_door
                ObjectType.TOILET -> R.drawable.obj_toilet
                ObjectType.COUNTER -> R.drawable.obj_casher
                ObjectType.WINDOW -> R.drawable.obj_window
                ObjectType.TABLE -> R.drawable.obj_table
                else -> R.drawable.obj_seat  // 기본값
            }
            val bitmap = getBitmapFromDrawable(resId)

            val cx = it.x * gridSize
            val cy = it.y * gridSize

            canvas.drawBitmap(bitmap, cx, cy, null)
        }
    }

    private fun getBitmapFromDrawable(resId: Int): android.graphics.Bitmap {
        val drawable = ContextCompat.getDrawable(context, resId)!!
        val bitmap = android.graphics.Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            android.graphics.Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun createInteractiveObjectView(x: Float, y: Float, type: ObjectType): ImageView {
        val drawableResId = getDrawableResId(type)
        return ImageView(context).apply {
            setImageResource(drawableResId)
            layoutParams = LayoutParams(130, 130).apply {
                leftMargin = (x * gridSize).toInt()
                topMargin = (y * gridSize).toInt()
            }
            tag = type

            var dX = 0f
            var dY = 0f
            val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    (parent as? ViewGroup)?.removeView(this@apply)
                    return true
                }
            })

            setOnTouchListener { v, event ->
                if (gestureDetector.onTouchEvent(event)) return@setOnTouchListener true

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = event.rawX - v.x
                        dY = event.rawY - v.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX - dX
                        val newY = event.rawY - dY
                        val params = layoutParams as LayoutParams
                        params.leftMargin = newX.toInt()
                        params.topMargin = newY.toInt()
                        layoutParams = params
                    }
                }
                true
            }
        }
    }

    fun addInteractiveObject(x: Float, y: Float, type: ObjectType) {
        if (mode != Mode.ADMIN) return  // 관리자일 때만 추가 가능
        val objectView = createInteractiveObjectView(x, y, type)
        objectView.tag = type
        addView(objectView)
    }

    private fun getDrawableResId(type: ObjectType): Int = when (type) {
        ObjectType.SEAT -> R.drawable.obj_seat
        ObjectType.DOOR -> R.drawable.obj_door
        ObjectType.TOILET -> R.drawable.obj_toilet
        ObjectType.COUNTER -> R.drawable.obj_casher
        ObjectType.WINDOW -> R.drawable.obj_window
        ObjectType.TABLE -> R.drawable.obj_table
    }

    fun extractObjectsForSave(): FloorDetail {
        val seatList = mutableListOf<Position>()
        val doorList = mutableListOf<Position>()
        val toiletList = mutableListOf<Position>()
        val windowList = mutableListOf<Position>()
        val counterList = mutableListOf<Position>()
        val tableList = mutableListOf<Position>()

        for (i in 0 until childCount) {
            val view = getChildAt(i) as? ImageView ?: continue
            val type = view.tag as? CanvasView.ObjectType ?: continue
            val x = ((view.x + view.width / 2f) / gridSize).toInt()
            val y = ((view.y + view.height / 2f) / gridSize).toInt()
            val pos = Position(x, y)

            when (type) {
                ObjectType.SEAT -> seatList.add(pos)
                ObjectType.DOOR -> doorList.add(pos)
                ObjectType.TOILET -> toiletList.add(pos)
                ObjectType.COUNTER -> counterList.add(pos)
                ObjectType.WINDOW -> windowList.add(pos)
                ObjectType.TABLE -> tableList.add(pos)
            }
        }

        return FloorDetail(
            borderPosition = listOf(),
            seatPosition = seatList,
            doorPosition = doorList,
            counterPosition = counterList,
            toiletPosition = toiletList,
            windowPosition = windowList
        )
    }
}