package dev.kourosh.baseapp

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.telephony.SmsManager
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun View.gone() {
    if (isVisible())
        visibility = View.GONE
}

fun View.visible() {
    if (isInvisible())
        visibility = View.VISIBLE
}

fun View.invisible() {
    if (isVisible())
        visibility = View.INVISIBLE
}

fun View.goneWithAnimation() {
    if (isVisible()) {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
        this.gone()
    }
}

fun View.visibleWithAnimation() {
    if (isInvisible()) {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        this.visible()
    }
}

fun View.invisibleWithAnimation() {
    if (isVisible()) {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
        this.invisible()
    }
}

fun View.isVisible() = visibility == View.VISIBLE
fun View.isInvisible() = (visibility == View.INVISIBLE || visibility == View.GONE)

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

/*fun ImageView.load(dir: Any?, @DrawableRes resourceId: Int = android.R.drawable.ic_menu_gallery, listener: RequestListener<Drawable?>) {
    Glide.with(context)
            .load(dir)
            .listener(listener)
            .apply(RequestOptions().placeholder(resourceId))
            .thumbnail(0.1f)
            .into(this)
}*/

fun TextView.moneyFormat() {
    if (this is EditText) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                removeTextChangedListener(this)
                setText(s.toString().moneyFormat())
                addTextChangedListener(this)
            }
        })
    } else {
        text = this.text.toString()
            .moneyFormat()
    }
}

val TextView.textString get() = this.text.toString()

fun String.moneyFormat(): String {
    var currentString = this
    if (currentString.isEmpty()) currentString = "0"
    return try {
        if (currentString.contains('.')) {
            NumberFormat.getNumberInstance(Locale.getDefault())
                .format(currentString.replace(",", "").toDouble())
        } else {
            NumberFormat.getNumberInstance(Locale.getDefault())
                .format(currentString.replace(",", "").toLong())
        }
    } catch (a: Exception) {
        "0"
    }
}

val Long.moneyFormat get() = NumberFormat.getNumberInstance(Locale.getDefault()).format(this)

val Double.moneyFormat get() = NumberFormat.getNumberInstance(Locale.getDefault()).format(this)

val Int.dp get():Int = this * (Resources.getSystem().displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

val Float.dp get() = (this * (Resources.getSystem().displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))

fun FragmentManager.size() = this.backStackEntryCount

fun FragmentManager.clearBackStack() = run {
    var size = this.size()
    while (size > 0) {
        this.popBackStack()
        size--
    }
}

fun FragmentManager.isLastFragment() = size() == 1

fun FragmentManager.isEmptyBackstack() = size() == 0

fun String.isNationalId(): Boolean {
    var zero = ""
    val current: String
    when {
        (length < 10) -> {
            for (i in 1..(10 - length)) {
                zero += "0"
            }
            current = "$zero$this"
        }

        (length == 10) -> current = this

        else -> return false
    }
    var sum = 0
    current.forEachIndexed { i, c ->
        if (i != 9) sum += (10 - i) * c.toString().toInt()
    }
    val control = sum % 11
    return if (control < 2) {
        control.toString() == current[9].toString()
    } else {
        (11 - control).toString() == current[9].toString()
    }
}

fun ByteArray.encodeBase64() = android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)
fun ByteArray.decodeBase64() = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
fun String.encodeBase64() = this.toByteArray().encodeBase64()
fun String.decodeBase64() = android.util.Base64.decode(this, android.util.Base64.DEFAULT)

fun SpannableStringBuilder.appendGray(grayString: String, context: Context) {
    val warmGrayTwo = ContextCompat.getColor(context, R.color.warm_grey)
    val start = length
    append(grayString)
    setSpan(ForegroundColorSpan(warmGrayTwo), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.appendBold(boldString: String) {
    val start = length
    append(boldString)
    setSpan(
        StyleSpan(android.graphics.Typeface.BOLD),
        start,
        length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun sendTextMessage(destinationAddress: String, message: String) {
    SmsManager.getDefault().sendTextMessage(destinationAddress, null, message, null, null)
}


fun launchMain(block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        block(this)
    }
}


fun CoroutineScope.onMain(block: suspend CoroutineScope.() -> Unit) {
    launch(Dispatchers.Main) {
        block(this)
    }
}


fun String.emptyToNull(): String? {
    return if (isNullOrBlank() || isEmpty())
        null
    else
        this
}

fun String.emptyOrZeroToNull(): String? {
    return if (isNullOrBlank() || isEmpty() || this.toLongOrNull() == 0L || this.toLongOrNull() == null)
        null
    else
        this
}



fun String.numP2E(reverse: Boolean = false): String {
    var str = this
    val chars = arrayOf(
        arrayOf("0", "۰"),
        arrayOf("1", "۱"),
        arrayOf("2", "۲"),
        arrayOf("3", "۳"),
        arrayOf("4", "۴"),
        arrayOf("5", "۵"),
        arrayOf("6", "۶"),
        arrayOf("7", "۷"),
        arrayOf("8", "۸"),
        arrayOf("9", "۹")
    )

    for (num in chars) {
        str = if (!reverse) {
            str.replace(num[1], num[0])
        } else {
            str.replace(num[0], num[1])
        }
    }
    //    Log.v("numE2P", str);
    return str
}
fun ObservableBoolean.start() {
    set(true)
}

fun ObservableBoolean.stop() {
    set(false)
}