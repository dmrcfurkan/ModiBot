package com.example.modibot.User.Payment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cardNumber: String = ""
    private var expirationDate: String = ""
    private val paint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        isAntiAlias = true
    }

    fun setCardNumber(number: String) {
        cardNumber = number
        invalidate() // Görünümü yeniden çizmek için
    }

    fun setExpirationDate(expiration: String) {
        expirationDate = expiration
        invalidate() // Görünümü yeniden çizmek için
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Test için bir çerçeve çiz
        val borderPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)

        // Kart numarasını çiz
        val yPosition = height / 2f + 80f // Kart numarasını biraz aşağıya kaydırıyoruz
        canvas?.drawText(
            cardNumber.chunked(4).joinToString(" "),
            90f,
            yPosition,
            paint
        )

        // Son Kullanma Tarihini Çiz
        val expirationText = "VALID THRU $expirationDate"

        val expirationPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f
            isAntiAlias = true
        }

        // Son kullanma tarihi için X ve Y koordinatlarını ayarlayalım
        val expirationX = 50f  // X konumu (kartın soluna yakın)
        val expirationY = yPosition + 60f // Kart numarasının altına yerleştirecek şekilde Y konumunu ayarlıyoruz

        // Son Kullanma Tarihi'ni çizerken X ve Y koordinatlarını kullan
        canvas?.drawText(expirationText, expirationX, expirationY, expirationPaint)



    }


}