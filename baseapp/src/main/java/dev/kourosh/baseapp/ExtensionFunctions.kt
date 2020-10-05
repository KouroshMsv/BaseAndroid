package dev.kourosh.baseapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.SmsManager
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.text.inSpans
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.logE
import dev.kourosh.basedomain.logI
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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

fun TextView.getCurrencyFormatListener() = object : TextWatcher {
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
        text = if (s?.toString().isNullOrBlank()) {
            ""
        } else {
            s.toString().currencyFormat
        }
        if (this@getCurrencyFormatListener is EditText) {
            setSelection(text.toString().length)
        }
        addTextChangedListener(this)
    }
}

fun TextView.setCurrencyFormat() {
    addTextChangedListener(getCurrencyFormatListener())
}

fun String.clearCurrencyFormat() = replace(",", "")


val String.currencyFormat: String
    get() {
        var currentString = this
        if (currentString.isEmpty()) currentString = "0"
        return try {
            if (currentString.contains('.')) {
                NumberFormat.getNumberInstance(Locale.ENGLISH)
                        .format(currentString.replace(",", "").toDouble())
            } else {
                NumberFormat.getNumberInstance(Locale.ENGLISH)
                        .format(currentString.replace(",", "").toLong())
            }
        } catch (a: Exception) {
            "0"
        }
    }

val String.isMobileNumber: Boolean
    get() {
        val pattern = "(09)\\d\\d\\d\\d\\d\\d\\d\\d\\d"
        return Pattern.matches(pattern, this)
    }

val Long.currencyFormat get() = NumberFormat.getNumberInstance(Locale.ENGLISH).format(this)

val Double.currencyFormat get() = NumberFormat.getNumberInstance(Locale.ENGLISH).format(this)

val Int.dp get():Int = this * (Resources.getSystem().displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

val Float.dp get() = (this * (Resources.getSystem().displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))

val String.isNationalId: Boolean
    get() {
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

val ByteArray.encodeBase64: String
    get() = android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)
val String.encodeBase64: String
    get() = this.toByteArray().encodeBase64

val ByteArray.decodeBase64: ByteArray
    get() = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
val String.decodeBase64: ByteArray
    get() = android.util.Base64.decode(this, android.util.Base64.DEFAULT)


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
    var firstIndex = 1
    var secondIndex = 0
    if (reverse) {
        firstIndex = 0
        secondIndex = 1
    }
    for (num in chars) {
        str = str.replace(num[firstIndex], num[secondIndex])
    }
    return str
}

fun ObservableBoolean.start() {
    set(true)
}

fun ObservableBoolean.stop() {
    set(false)
}


suspend fun <T : Any> Result<T>.parseOnMain(
    loading: ObservableBoolean,
    success: (data: T) -> Unit,
    error: (message: String, errorCode: ErrorCode) -> Unit
) {
    when (val result = this) {
        is Result.Success -> {
            loading.stop()
            withContext(Dispatchers.Main) {
                success(result.data)
            }
        }
        is Result.Error -> {
            loading.stop()
            logE("""message: [${result.message}]|statusCode: [${result.errorCode}""".trimMargin())
            withContext(Dispatchers.Main) {
                error(result.message, result.errorCode)
            }
        }
    }
}

suspend fun <T : Any> Result<T>.parseWithoutErrorOnMain(loading: ObservableBoolean, success: (data: T) -> Unit) {
    when (val result = this) {
        is Result.Success -> {
            loading.stop()
            withContext(Dispatchers.Main) {
                success(result.data)
            }
        }
        is Result.Error -> {
            loading.stop()
            result.log()
        }
    }
}

fun <T> RecyclerView.Adapter<*>.autoNotify(oldList: List<T>, newList: List<T>, areItemsTheSameCompare: (T, T) -> Boolean, areContentsTheSameCompare: (T, T) -> Boolean = { t1, t2 -> t1 == t2 }) {
    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSameCompare(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSameCompare(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size
    })

    diff.dispatchUpdatesTo(this)
}


@SuppressLint("ResourceAsColor")
inline fun SpannableStringBuilder.hint(text: () -> String) = inSpans(ForegroundColorSpan(R.color.baseAppHintColor), builderAction = { append(text()) })

inline fun SpannableStringBuilder.bold(text: () -> String) = inSpans(StyleSpan(Typeface.BOLD), builderAction = { append(text()) })


fun <T> ObservableField<T>.observe(observer: (T?) -> (Unit)) {
    addOnPropertyChangedCallback(object :
            androidx.databinding.Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
            observer(get())
        }
    })
}

fun ObservableBoolean.observe(observer: (Boolean) -> (Unit)) {
    addOnPropertyChangedCallback(object :
            androidx.databinding.Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
            observer(get())
        }
    })
}

const val enter = "\n"
const val halfSpace = "\u200c"
fun SpannableStringBuilder.enter() = append(enter)
fun SpannableStringBuilder.halfSpace() = append(halfSpace)


fun Bitmap.compress(maxWidthOrHeight: Int): Bitmap {
    val builderString =
            StringBuilder("width: $width height: $height density: $density byteCount: $byteCount")
    val biggerIsHeight = height > width
    val scale =
            BigDecimal(if (biggerIsHeight) height.toDouble() / width.toDouble() else width.toDouble() / height.toDouble())
    builderString.append("\n")
    builderString.append("scale: $scale")

    val max = BigDecimal(maxWidthOrHeight.toDouble())
    val width = if (biggerIsHeight) max.divide(scale, 2, RoundingMode.HALF_UP) else max
    val height = if (biggerIsHeight) max else max.divide(scale, 2, RoundingMode.HALF_UP)
    val newbitmap = Bitmap.createScaledBitmap(this, width.toInt(), height.toInt(), false)
    builderString.append("\n")
    builderString.append("new width: ${newbitmap.width} height: ${newbitmap.height}  byteCount: ${newbitmap.byteCount}")
    builderString.append("\n")
    Log.d("Kourosh", builderString.toString())
    return newbitmap
}

fun Uri.bitmap(contentResolver: ContentResolver, decodeException: OnDecodeBitmapException): Bitmap? {
    var exception1: Exception? = null
    var exception2: Exception? = null
    var exception3: Exception? = null
    val bitmap = try {
        BitmapFactory.decodeStream(contentResolver.openInputStream(this))
    } catch (e1: Exception) {
        exception1 = e1
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, this))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, this)
            }
        } catch (e2: Exception) {
            exception2 = e2
            try {
                MediaStore.Images.Media.getBitmap(contentResolver, this)
            } catch (e3: Exception) {
                exception3 = e3
                null
            }
        }
    }
    decodeException.onException(exception1, exception2, exception3)
    return bitmap

}

interface OnDecodeBitmapException {
    fun onException(e1: Exception?, e2: Exception?, e3: Exception?)
}

fun RecyclerView.ViewHolder.getColor(@ColorRes id: Int) = ContextCompat.getColor(itemView.context, id)


@Throws(IOException::class)
fun compress(context: Context, photoURI: Uri, maxWidthOrHeight: Int, decodeException: OnDecodeBitmapException) {
    val compressedBitmap =
            photoURI.bitmap(context.contentResolver, decodeException)?.run {
                logI("normal image size= $byteCount")
                compress(maxWidthOrHeight)
            }
    if (compressedBitmap != null) {
        var outputStream: OutputStream? = null
        try {
            outputStream = context.contentResolver.openOutputStream(photoURI)
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        } finally {
            outputStream?.close()
        }
        logI("compressed image size= ${compressedBitmap.byteCount}")
    }
}


@Throws(IOException::class)
fun createImageFile(
    context: Context,
    prefix: String = "JPEG",
    storageDir: File = context.filesDir
): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
    return File(storageDir, "${prefix}_${timeStamp}.jpg")
}

/**
 * * photoURI =FileProvider.getUriForFile(context, authority, file)
 */
fun Fragment.dispatchTakePictureIntent(cameraRequestCode: Int, photoURI: Uri) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, cameraRequestCode)
    }
}
