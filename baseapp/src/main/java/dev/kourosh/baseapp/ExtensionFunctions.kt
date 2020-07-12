package dev.kourosh.baseapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
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
import androidx.fragment.app.FragmentManager
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

/*fun ImageView.load(dir: Any?, @DrawableRes resourceId: Int = android.R.drawable.ic_menu_gallery, listener: RequestListener<Drawable?>) {
    Glide.with(context)
            .load(dir)
            .listener(listener)
            .apply(RequestOptions().placeholder(resourceId))
            .thumbnail(0.1f)
            .into(this)
}*/
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
            s.toString().currencyFormat()
        }
        if (this@getCurrencyFormatListener is EditText) {
            setSelection(text.toString().length)
        }
        addTextChangedListener(this)
    }
}

fun TextView.currencyFormat() {
    addTextChangedListener(getCurrencyFormatListener())
}

fun String.clearCurrencyFormat() = replace(",", "")


fun String.currencyFormat(): String {
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

fun String.isMobileNumber(): Boolean {
    val pattern = "(09)\\d\\d\\d\\d\\d\\d\\d\\d\\d"
    return Pattern.matches(pattern, this)
}

val Long.currencyFormat get() = NumberFormat.getNumberInstance(Locale.ENGLISH).format(this)

val Double.currencyFormat get() = NumberFormat.getNumberInstance(Locale.ENGLISH).format(this)

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

suspend fun <T : Any> Result<T>.parseWithoutErrorOnMain(
    loading: ObservableBoolean,
    success: (data: T) -> Unit
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
            result.log()
        }
    }
}

fun <T> RecyclerView.Adapter<*>.autoNotify(
    oldList: List<T>,
    newList: List<T>,
    areItemsTheSameCompare: (T, T) -> Boolean,
    areContentsTheSameCompare: (T, T) -> Boolean = { t1, t2 -> t1 == t2 }
) {
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
inline fun SpannableStringBuilder.hint(
    text: () -> String
) = inSpans(ForegroundColorSpan(R.color.hintColor), builderAction = { append(text()) })

inline fun SpannableStringBuilder.bold(
    text: () -> String
) = inSpans(StyleSpan(Typeface.BOLD), builderAction = { append(text()) })


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
fun SpannableStringBuilder.enter() = append(enter)
fun SpannableStringBuilder.halfSpace() = append(halfSpace)
const val halfSpace = "\u200c"


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

fun Uri.bitmap(contentResolver: ContentResolver): Bitmap? {
    return MediaStore.Images.Media.getBitmap(contentResolver, this)
}

fun RecyclerView.ViewHolder.getColor(@ColorRes id: Int) =
    ContextCompat.getColor(itemView.context, id)


@Throws(IOException::class)
fun compress(context: Context, photoURI: Uri, maxWidthOrHeight: Int) {
    val compressedBitmap =
        photoURI.bitmap(context.contentResolver)?.run {
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
    prefix: String = "JPEG"
): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "${prefix}_${timeStamp}", ".jpg",
        storageDir /* directory */
    )/*.apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }*/
}

/**
 * * photoURI =FileProvider.getUriForFile(context, authority, file)
 */
fun Fragment.dispatchTakePictureIntent(
    cameraRequestCode: Int,
    photoURI: Uri
) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, cameraRequestCode)
    }
}
