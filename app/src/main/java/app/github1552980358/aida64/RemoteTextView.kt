package app.github1552980358.aida64

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.util.logE
import lib.github1552980358.ktExtension.jvm.keyword.tryCatch
import kotlin.math.abs
import kotlin.math.absoluteValue

class RemoteTextView: View {
    
    companion object {
        
        private const val TAG = "RemoteTextView"
        
        private const val COLON = ':'
    
    }
    
    private var target: String? = null
        set(value) {
            field = value
            postInvalidate()
        }
    
    fun setTarget(ip: String, port: String) {
        target = "$ip$COLON$port"
    }
    
    var status: String? = null
        set(value) {
            field = value
            postInvalidate()
        }
    
    fun setStatus(resId: Int) {
        status = context.getString(resId)
    }
    
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }
    
    private val rectTarget = Rect()
    private val rectStatus = Rect()
    
    var delayMiles = 5000L
    private var textMove = false
    private var job: Job? = null
    
    constructor(context: Context): super(context)
    
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)
    
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int):
        super(context, attributeSet, defStyleAttr)
    
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int):
        super(context, attributeSet, defStyleAttr, defStyleRes)
    
    init {
        paint.textSize = context.resources.getDimension(R.dimen.remoteTextView_title)
        status = context.getString(R.string.remoteActivity_connecting)
    }
    
    override fun draw(canvas: Canvas?) {
        logE(TAG, "View: onDraw")
        
        super.draw(canvas)
    
        canvas ?: return
        
        target?:return
        status?:return
    
        paint.getTextBounds(target!!, 0, target!!.length, rectTarget)
        val targetHeight = abs(rectTarget.height())
        val targetWidth = abs(rectTarget.width())
    
        paint.getTextBounds(status!!, 0, status!!.length, rectStatus)
        val statusHeight = rectStatus.height().absoluteValue
        val statusWidth = rectStatus.width().absoluteValue
        
        val maxWidth = width - if (targetWidth > statusWidth) targetWidth else statusWidth
        val maxHeight = height - targetHeight - statusHeight - statusHeight
        
        val textDiff = (targetWidth - statusWidth).absoluteValue / 2
        
        val randomWidth = (0 .. maxWidth).random().toFloat()
        val randomHeight = (0 .. maxHeight).random().toFloat()
        
        canvas.drawText(target!!, randomWidth + if (targetWidth >= statusWidth) 0 else textDiff, randomHeight , paint)
        canvas.drawText(status!!, randomWidth + if (targetWidth <= statusWidth) 0 else textDiff, randomHeight + statusHeight, paint)
        
    }
    
    fun startTextMove() {
        textMove = true
        tryCatch { job?.cancel() }
        job = GlobalScope.launch {
            while (textMove) {
                postInvalidate()
                delay(delayMiles)
            }
        }
    }
    
    fun stopTextMove() {
        textMove = false
        tryCatch { job?.cancel() }
        job = null
    }
    
    fun amoledMode() {
        setBackgroundColor(Color.BLACK)
        paint.color = Color.WHITE
    }
    
}