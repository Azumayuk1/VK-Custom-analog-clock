package ru.mirea.vkcustomclock.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color

import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import ru.mirea.vkcustomclock.R
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomAnalogClock(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    // Получение атрибутов
    private val attributes =
        context?.obtainStyledAttributes(attributeSet, R.styleable.CustomAnalogClock)

    private var clockHeight = 0
    private var clockWidth = 0
    private var clockRadius = 0

    private var clockCentreHorizontal = 0
    private var clockCentreVertical = 0
    private var clockPadding = 0
    private var isInitialized = false

    private var colorHourAndMinuteHands = Color.BLACK
    private var colorSecondHand = Color.RED
    private var colorNumerals = Color.BLACK
    private var colorCircle = Color.BLACK

    private lateinit var paintCircle: Paint
    private lateinit var paintSecondHand: Paint
    private lateinit var paintMinuteHand: Paint
    private lateinit var paintHourHand: Paint

    private lateinit var rect: Rect
    private lateinit var numbers: IntArray

    private var hour = 0f
    private var minute = 0f
    private var second = 0f


    private var hourHandSize = 0
    private var minuteHandSize = 0
    private var numberFontSize = 0

    /**
     * Инициализация, происходит только один раз
     */
    private fun init() {
        // Высота и ширина View
        clockHeight = height
        clockWidth = width

        // Отступ циферблата от границы View
        clockPadding =
            attributes?.getInt(R.styleable.CustomAnalogClock_clockPadding, 10) ?: 10

        // Центр часов
        clockCentreHorizontal = clockWidth / 2
        clockCentreVertical = clockHeight / 2

        clockRadius = min(clockHeight, clockWidth) / 2 - clockPadding

        // Получение цветов
        colorHourAndMinuteHands =
            attributes?.getColor(
                R.styleable.CustomAnalogClock_colorHourAndMinuteHands,
                Color.BLACK
            ) ?: Color.BLACK

        colorSecondHand =
            attributes?.getColor(R.styleable.CustomAnalogClock_colorSecondHand, Color.RED)
                ?: Color.RED

        colorNumerals =
            attributes?.getColor(
                R.styleable.CustomAnalogClock_colorNumerals,
                Color.BLACK
            ) ?: Color.BLACK

        colorCircle =
            attributes?.getColor(
                R.styleable.CustomAnalogClock_colorCircle,
                Color.BLACK
            ) ?: Color.BLACK


        // Кисти для рисования стрелок и циферблата
        paintCircle = getPaint(colorCircle, 10)
        paintSecondHand = getPaint(colorSecondHand, 6)
        paintMinuteHand = getPaint(colorHourAndMinuteHands, 10)
        paintHourHand = getPaint(colorHourAndMinuteHands, 16)

        rect = Rect()

        // Размер шрифта
        numberFontSize = clockHeight / 20

        // Длина стрелок
        hourHandSize = clockRadius - clockRadius / 2
        minuteHandSize = clockRadius - clockRadius / 4

        // Цифры
        numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

        isInitialized = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized) {
            init()
        }

        drawCircle(canvas)
        drawNumerals(canvas)
        drawHands(canvas)

        // Обновление каждую секунду
        postInvalidateDelayed(1000)
    }

    /**
     * Получение кисти
     * @param color Цвет кисти
     * @param strokeWidth Ширина кисти
     */
    private fun getPaint(color: Int = Color.BLACK, strokeWidth: Int): Paint {
        val paint = Paint()

        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
        paint.isAntiAlias = true

        return paint
    }

    /**
     * Кисть для текста (чисел)
     * @param color Цвет кисти
     * @param textSize Размер шрифта
     * @param number Число
     */
    private fun getNumberPaint(color: Int = Color.BLACK, textSize: Int, number: String): Paint {
        val paint = Paint()

        paint.textSize = textSize.toFloat()
        paint.getTextBounds(number, 0, number.length, rect)
        paint.color = color


        return paint
    }

    /**
     * Нарисовать круг (контейнер часов)
     */
    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(
            clockCentreHorizontal.toFloat(), clockCentreVertical.toFloat(),
            clockRadius.toFloat(), paintCircle
        )
    }

    /**
     * Нарисовать числа
     */
    private fun drawNumerals(canvas: Canvas) {
        for (number in numbers) {
            val numberString = number.toString()

            // Рассчет положения числа
            val angle = Math.PI / 6 * (number - 3)

            val x =
                (clockCentreHorizontal + cos(angle) *
                        (clockRadius - numberFontSize) - rect.width() / 2).toInt()
            val y =
                (clockCentreVertical + sin(angle) *
                        (clockRadius - numberFontSize) + rect.height() / 2).toInt()

            canvas.drawText(
                numberString,
                x.toFloat(),
                y.toFloat(),
                getNumberPaint(colorNumerals, numberFontSize, numberString)
            )
        }
    }

    /**
     * Получить угол стрелки
     * @param numberPointedTo На какое число должна указывать стрелка
     */
    private fun getHandAngle(numberPointedTo: Float): Float {
        return (Math.PI * numberPointedTo / 30 - Math.PI / 2).toFloat()
    }

    /**
     * Нарисовать часовую стрелку
     * @param numberPointedTo На какое число должна указывать стрелка
     */
    private fun drawHourHand(canvas: Canvas, numberPointedTo: Float) {
        val handAngle = getHandAngle(numberPointedTo)

        canvas.drawLine(
            clockCentreHorizontal.toFloat(),
            clockCentreVertical.toFloat(),
            (clockCentreHorizontal + cos(handAngle) * hourHandSize),
            (clockCentreVertical + sin(handAngle) * hourHandSize),
            paintHourHand
        )
    }

    /**
     * Нарисовать минутную стрелку
     * @param numberPointedTo На какое число должна указывать стрелка
     */
    private fun drawMinuteHand(canvas: Canvas, numberPointedTo: Float) {
        val handAngle = getHandAngle(numberPointedTo)

        canvas.drawLine(
            clockCentreHorizontal.toFloat(),
            clockCentreVertical.toFloat(),
            (clockCentreHorizontal + cos(handAngle) * minuteHandSize),
            (clockCentreVertical + sin(handAngle) * hourHandSize),
            paintMinuteHand
        )
    }


    /**
     * Нарисовать секундную стрелку
     * @param numberPointedTo На какое число должна указывать стрелка
     */
    private fun drawSecondsHand(canvas: Canvas, numberPointedTo: Float) {
        val handAngle = getHandAngle(numberPointedTo)

        canvas.drawLine(
            clockCentreHorizontal.toFloat(),
            clockCentreVertical.toFloat(),
            (clockCentreHorizontal + cos(handAngle) * minuteHandSize),
            (clockCentreVertical + sin(handAngle) * hourHandSize),
            paintSecondHand
        )
    }

    /**
     * Нарисовать все стрелки часов
     */
    private fun drawHands(canvas: Canvas) {
        // Получение времени
        val calendar = Calendar.getInstance()

        hour =
            calendar[Calendar.HOUR_OF_DAY].toFloat()

        // Перевод часов в 12-часовой формат
        hour = if (hour > 12) hour - 12 else hour

        minute = calendar[Calendar.MINUTE].toFloat()

        second = calendar[Calendar.SECOND].toFloat()

        drawHourHand(canvas, ((hour + minute / 60.0) * 5f).toFloat())
        drawMinuteHand(canvas, minute)
        drawSecondsHand(canvas, second)
    }
}