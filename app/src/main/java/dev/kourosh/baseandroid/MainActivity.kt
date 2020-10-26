package dev.kourosh.baseandroid

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import dev.kourosh.baseapp.enums.MessageType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            showSnackBar(btn, applicationContext, "Kourosh", MessageType.INFO)
        }
    }
    fun showSnackBar(view: View, context: Context, message: String, type: MessageType, duration: Int = Snackbar.LENGTH_LONG) {
        val snackBar = Snackbar.make(view, message, duration)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(context, type.backgroundColor))
        val tv = snackBar.view.findViewById(dev.kourosh.baseapp.R.id.snackbar_text) as TextView
        tv.setTextColor(ContextCompat.getColor(context, type.textColor))
        val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackBar.view.layoutParams = params
        ViewCompat.setLayoutDirection(snackBar.view, ViewCompat.LAYOUT_DIRECTION_RTL)
        snackBar.show()
    }
}
