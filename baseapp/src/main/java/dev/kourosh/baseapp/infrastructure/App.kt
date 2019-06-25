package dev.kourosh.baseapp.infrastructure

import android.app.Application
import dev.kourosh.baseapp.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPump.init


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        init(ViewPump.builder().addInterceptor(CalligraphyInterceptor(CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.isRegular)).setFontAttrId(R.attr.fontPath).build())).build())
    }

}