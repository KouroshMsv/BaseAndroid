package dev.kourosh.baseapp

import android.view.View
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import me.dm7.barcodescanner.zbar.ZBarScannerView
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler

@BindingAdapter("onResultReceive")
fun ZBarScannerView.setResultHandler2(resultHandler: ResultHandler) {
    setResultHandler(resultHandler)
}

/*@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(imageUrl: String?) {
    Picasso.get()
        .load(imageUrl)
        .into(this)
}

@BindingAdapter("startShimmer")
fun ShimmerFrameLayout.setState(isStart: Boolean) {
    if (isStart) {
        visibleWithAnimation()
        startShimmer()
    } else {
        invisibleWithAnimation()
        stopShimmer()
    }
}
*/

@BindingAdapter("visibleGone")
fun View.setVisibleGone(visible: Boolean) {
    if (visible) {
        visible()
    } else {
        gone()
    }
}

@BindingAdapter("visibleInvisible")
fun View.setVisibleInvisible(visible: Boolean) {
    if (visible) {
        visible()
    } else {
        invisible()
    }
}

@BindingAdapter("visibleGoneWithAnimation")
fun View.setVisibleGoneWithAnimation(visible: Boolean) {
    if (visible) {
        visibleWithAnimation()
    } else {
        goneWithAnimation()
    }
}

@BindingAdapter("visibleInvisibleWithAnimation")
fun View.setVisibleInvisibleWithAnimation(visible: Boolean) {
    if (visible) {
        visibleWithAnimation()
    } else {
        invisibleWithAnimation()
    }
}

@BindingAdapter("startShimmer")
fun ShimmerFrameLayout.setState(isStart: Boolean) {
    if (isStart) {
        visibleWithAnimation()
        startShimmer()
    } else {
        invisibleWithAnimation()
        stopShimmer()
    }
}
