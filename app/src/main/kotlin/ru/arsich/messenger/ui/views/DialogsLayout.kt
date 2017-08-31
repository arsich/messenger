package ru.arsich.messenger.ui.views

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.arsich.messenger.R

class DialogsLayout : ViewGroup {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initValues(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initValues(context, attrs)
    }

    private lateinit var datePaint: Paint

    private var horizontalPadding: Int = 0

    private var bottomCenterMargin: Int = 0

    private var avatarChild: ImageView? = null
    private var avatarSize: Int = 0
    private var avatarBottomMargin = 0

    private var dateChild: TextView? = null
    private var dateWidth: Int = 0
    private var dateHeight: Int = 0
    private var dateText: String = ""
    private var dateTextSize: Int = 16

    private var titleChild: TextView? = null
    private var titleHeight: Int = 0

    private var messageChild: TextView? = null
    private var messageHeight: Int = 0

    private fun initValues(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogsLayout, 0, 0)

        try {
            horizontalPadding = a.getDimensionPixelSize(R.styleable.DialogsLayout_vg_horizontalPadding, horizontalPadding)
            avatarSize = a.getDimensionPixelSize(R.styleable.DialogsLayout_vg_avatarSize, avatarSize)
            dateTextSize = a.getDimensionPixelSize(R.styleable.DialogsLayout_vg_dateTextSize, dateTextSize)
            dateText = a.getString(R.styleable.DialogsLayout_vg_dateTextPattern)
            bottomCenterMargin = a.getDimensionPixelSize(R.styleable.DialogsLayout_vg_bottomCenterMargin, bottomCenterMargin)
            avatarBottomMargin = a.getDimensionPixelSize(R.styleable.DialogsLayout_vg_bottomAvatarMargin, avatarBottomMargin)
        } finally {
            a.recycle()
        }

        datePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        datePaint.textSize = dateTextSize.toFloat()
        dateWidth = datePaint.measureText(dateText).toInt()
    }

    fun setDateText(text: String) {
        dateText = text
        dateChild?.text = text
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val totalHeight = heightMeasureSpec

        if (avatarChild == null) {
            avatarChild = findViewById<ImageView>(R.id.avatarView)
        }
        avatarChild?.measure(MeasureSpec.makeMeasureSpec(avatarSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(avatarSize, MeasureSpec.EXACTLY))

        if (dateChild == null) {
            dateChild = findViewById<TextView>(R.id.dateView)
        }

        dateChild?.let {
            it.measure(MeasureSpec.makeMeasureSpec(dateWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(totalHeight - bottomCenterMargin, MeasureSpec.AT_MOST))
            dateHeight = it.measuredHeight
            it.text = dateText
        }

        if (titleChild == null) {
            titleChild = findViewById<TextView>(R.id.titleView)
        }

        titleChild?.let {
            val width = totalWidth - dateWidth - horizontalPadding * 3 - avatarSize
            it.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(totalHeight - bottomCenterMargin, MeasureSpec.AT_MOST))
            titleHeight = it.measuredHeight
            // recalculate text (ellipsis end)
            it.text = it.text
        }

        if (messageChild == null) {
            messageChild = findViewById<TextView>(R.id.messageView)
        }

        messageChild?.let {
            val width = totalWidth - horizontalPadding * 3 - avatarSize
            it.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(bottomCenterMargin, MeasureSpec.AT_MOST))
            messageHeight = it.measuredHeight
            // recalculate text (ellipsis end)
            it.text = it.text
        }

        setMeasuredDimension(totalWidth, totalHeight)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val totalWidth = measuredWidth
        val totalHeight = measuredHeight
        avatarChild?.layout(horizontalPadding, totalHeight - avatarBottomMargin - avatarSize,
                horizontalPadding + avatarSize, totalHeight - avatarBottomMargin)

        dateChild?.layout(totalWidth - horizontalPadding - dateWidth, totalHeight - bottomCenterMargin - dateHeight,
                totalWidth - horizontalPadding, totalHeight - bottomCenterMargin)

        titleChild?.layout(horizontalPadding * 2 + avatarSize, totalHeight - bottomCenterMargin - titleHeight,
                totalWidth - 2 * horizontalPadding - dateWidth, totalHeight - bottomCenterMargin)

        messageChild?.layout(horizontalPadding * 2 + avatarSize, totalHeight - bottomCenterMargin,
                totalWidth - 2 * horizontalPadding, totalHeight)
    }
}