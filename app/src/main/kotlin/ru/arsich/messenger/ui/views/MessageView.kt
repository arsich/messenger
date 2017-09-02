package ru.arsich.messenger.ui.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import ru.arsich.messenger.R

class MessageView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initValues(context, attrs)
        initLayout()
    }

    private lateinit var incomingBubbleDrawable: Drawable
    private lateinit var incomingBubbleWithoutCornerDrawable: Drawable
    private lateinit var outgoingBubbleDrawable: Drawable
    private lateinit var outgoingBubbleWithoutCornerDrawable: Drawable

    private var messageText: String = ""
    private var messageTextSize: Int = 16
    private var messageTextColorIncoming: Int = 0
    private var messageTextColorOutgoing: Int = 0

    private var isIncomingMessage: Boolean = false
    private var isLastMessage: Boolean = false

    private lateinit var messagePaint: TextPaint
    private lateinit var messageLayout: StaticLayout

    private var speechBubbleCornerSize: Float= 0f
    private var speechBubbleHorizontalPadding: Float = 0f
    private var speechBubbleVerticalPadding: Float = 0f

    private var outgoingMessageTextLeft: Float = 0f
    private var incomingMessageTextLeft: Float = 0f

    private var dateText: String = ""
    private var dateTextSize: Int = 16
    private var dateTextColor: Int = 0
    private var datePadding: Float = 0f

    private lateinit var datePaint: TextPaint
    private lateinit var dateLayout: StaticLayout

    private var dateIncomingLeft: Float = 0f
    private var dateOutgoingLeft: Float = 0f
    private var dateTop: Float = 0f

    private var avatarSize: Int = 0
    private var avatarPaint: Paint? = null

    private var avatarRadius: Float = 0f
    private var avatarX: Float = 0f
    private var avatarY: Float = 0f

    private var minSpeechBubbleWidth = 0

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initValues(context, attrs)
        initLayout()
    }

    private fun initValues(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MessageView, 0, 0)

        try {
            isIncomingMessage = a.getBoolean(R.styleable.MessageView_v_isIncomingMessage, false)
            isLastMessage = a.getBoolean(R.styleable.MessageView_v_isLastMessage, false)
            incomingBubbleDrawable = a.getDrawable(R.styleable.MessageView_v_incomingSpeechBubble)
            incomingBubbleWithoutCornerDrawable = a.getDrawable(R.styleable.MessageView_v_incomingSpeechBubbleWithoutCorner)
            outgoingBubbleDrawable = a.getDrawable(R.styleable.MessageView_v_outgoingSpeechBubble)
            outgoingBubbleWithoutCornerDrawable = a.getDrawable(R.styleable.MessageView_v_outgoingSpeechBubbleWithoutCorner)

            messageText = a.getString(R.styleable.MessageView_v_messageText)
            messageTextSize = a.getDimensionPixelSize(R.styleable.MessageView_v_messageTextSize, 16)

            messageTextColorIncoming = ContextCompat.getColor(context, a.getResourceId(R.styleable.MessageView_v_messageTextColorIncoming, 0))
            messageTextColorOutgoing = ContextCompat.getColor(context, a.getResourceId(R.styleable.MessageView_v_messageTextColorOutgoing, 0))

            speechBubbleCornerSize = a.getDimensionPixelSize(R.styleable.MessageView_v_speechBubbleCornerSize, 0).toFloat()
            speechBubbleHorizontalPadding = a.getDimensionPixelSize(R.styleable.MessageView_v_speechBubbleHorizontalPadding, 0).toFloat()
            speechBubbleVerticalPadding = a.getDimensionPixelSize(R.styleable.MessageView_v_speechBubbleVerticalPadding, 0).toFloat()
            minSpeechBubbleWidth = a.getDimensionPixelSize(R.styleable.MessageView_v_minSpeechBubbleWidth, 0)

            dateText = a.getString(R.styleable.MessageView_v_dateText)
            dateTextSize = a.getDimensionPixelSize(R.styleable.MessageView_v_dateTextSize, 16)
            dateTextColor = ContextCompat.getColor(context, a.getResourceId(R.styleable.MessageView_v_dateTextColor, 0))
            datePadding = a.getDimensionPixelSize(R.styleable.MessageView_v_datePadding, 0).toFloat()

            avatarSize = a.getDimensionPixelSize(R.styleable.MessageView_v_avatarSize, 0)
        } finally {
            a.recycle()
        }
    }

    private fun initLayout() {
        initMessageText()
        initDateText()
    }

    private fun initMessageText() {
        messagePaint = TextPaint()
        messagePaint.isAntiAlias = true
        messagePaint.textSize = messageTextSize.toFloat()
        if (isIncomingMessage) {
            messagePaint.color = messageTextColorIncoming
        } else {
            messagePaint.color = messageTextColorOutgoing
        }

        val width = messagePaint.measureText(messageText).toInt()
        messageLayout = StaticLayout(messageText, messagePaint, width, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
    }

    private fun initDateText() {
        datePaint = TextPaint()
        datePaint.isAntiAlias = true
        datePaint.color = dateTextColor
        datePaint.textSize = dateTextSize.toFloat()

        val width = datePaint.measureText(dateText).toInt()
        dateLayout = StaticLayout(dateText, datePaint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
    }

    fun setMessageText(message: String) {
        messageText = message
        initMessageText()
    }

    fun setIsIncomingMessage(isIncoming: Boolean) {
        isIncomingMessage = isIncoming
    }

    fun setIsLastMessage(isLast: Boolean) {
        isLastMessage = isLast
    }

    fun setDateText(date: String) {
        dateText = date
        initDateText()
    }

    fun addAvatar(avatar: Bitmap) {
        val avatarShader = BitmapShader(avatar, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val matrix = Matrix()

        var scale = 0f
        var dx = 0f
        var dy = 0f

        if (avatar.width > avatar.height) {
            scale = avatarSize / avatar.height.toFloat()
            dx = (avatarSize - avatar.width * scale) * 0.5f
        } else {
            scale = avatarSize / avatar.width.toFloat()
            dy = (avatarSize - avatar.height * scale) * 0.5f
        }

        matrix.setScale(scale, scale)
        matrix.postTranslate(-dx, -dy)
        avatarShader.setLocalMatrix(matrix)

        avatarPaint = Paint()
        avatarPaint?.isAntiAlias = true
        avatarPaint?.shader = avatarShader
        invalidate()
    }

    fun clearAvatar() {
        avatarPaint = null
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthRequirement = MeasureSpec.getSize(widthMeasureSpec)

        val dateWidth = dateLayout.width + (datePadding * 2).toInt()

        // calculate text width
        var noTextSpaceWidth = paddingLeft + paddingRight + speechBubbleCornerSize.toInt() + speechBubbleHorizontalPadding.toInt() * 2 + dateWidth
        if (isIncomingMessage) {
            noTextSpaceWidth += avatarSize
        }
        var textWidth = messageLayout.width
        if (textWidth > widthRequirement - noTextSpaceWidth) {
            textWidth = widthRequirement - noTextSpaceWidth
            messageLayout = StaticLayout(messageText, messagePaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }

        val height = messageLayout.height + speechBubbleVerticalPadding.toInt() * 2

        // measure speech bubbles
        val speechBubbleWidth = Math.max(speechBubbleCornerSize.toInt() + speechBubbleHorizontalPadding.toInt() * 2 +  messageLayout.width, minSpeechBubbleWidth)

        val rightBubblePosition = paddingLeft + speechBubbleWidth + avatarSize
        incomingBubbleDrawable.setBounds(paddingLeft + avatarSize, 0, rightBubblePosition, height)
        incomingBubbleWithoutCornerDrawable.setBounds(paddingLeft + avatarSize + speechBubbleCornerSize.toInt(), 0, rightBubblePosition, height)

        val leftBubblePosition = widthRequirement - paddingRight - speechBubbleWidth
        outgoingBubbleDrawable.setBounds(leftBubblePosition, 0, widthRequirement - paddingRight, height)
        outgoingBubbleWithoutCornerDrawable.setBounds(leftBubblePosition, 0, widthRequirement - paddingRight - speechBubbleCornerSize.toInt(), height)

        // calculate text position
        incomingMessageTextLeft = paddingLeft + speechBubbleHorizontalPadding + speechBubbleCornerSize + avatarSize
        outgoingMessageTextLeft = leftBubblePosition + speechBubbleHorizontalPadding

        // calculate date position
        dateIncomingLeft = rightBubblePosition + datePadding
        dateOutgoingLeft = leftBubblePosition - datePadding - dateLayout.width
        dateTop = height - speechBubbleVerticalPadding - dateLayout.height

        // calculate avatar position
        avatarX = paddingLeft.toFloat()
        avatarY = paddingTop.toFloat()
        avatarRadius = (avatarSize / 2).toFloat()

        setMeasuredDimension(widthRequirement, height)
    }

    override fun onDraw(canvas: Canvas?) {
        if (isIncomingMessage) {
            drawIncomingMessage(canvas)
        } else {
            drawOutgoingMessage(canvas)
        }
    }

    private fun drawIncomingMessage(canvas: Canvas?) {
        // draw speech bubble
        if (isLastMessage) {
            incomingBubbleDrawable.draw(canvas)
        } else {
            incomingBubbleWithoutCornerDrawable.draw(canvas)
        }

        if (isLastMessage && avatarPaint != null) {
            canvas?.save()
            canvas?.translate(avatarX, avatarY)
            canvas?.drawCircle(avatarRadius, avatarRadius, avatarRadius, avatarPaint)
            canvas?.restore()
        }

        // draw message text
        canvas?.save()
        canvas?.translate(incomingMessageTextLeft, paddingTop + speechBubbleVerticalPadding)
        messageLayout.draw(canvas)
        canvas?.restore()

        // draw date text
        canvas?.save()
        canvas?.translate(dateIncomingLeft, dateTop)
        dateLayout.draw(canvas)
        canvas?.restore()
    }

    private fun drawOutgoingMessage(canvas: Canvas?) {
        // draw speech bubble
        if (isLastMessage) {
            outgoingBubbleDrawable.draw(canvas)
        } else {
            outgoingBubbleWithoutCornerDrawable.draw(canvas)
        }

        // draw message text
        canvas?.save()
        canvas?.translate(outgoingMessageTextLeft, paddingTop + speechBubbleVerticalPadding)
        messageLayout.draw(canvas)
        canvas?.restore()

        // draw date text
        canvas?.save()
        canvas?.translate(dateOutgoingLeft, dateTop)
        dateLayout.draw(canvas)
        canvas?.restore()
    }
}