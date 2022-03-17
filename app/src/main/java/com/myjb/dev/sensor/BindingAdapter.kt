package com.myjb.dev.sensor

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.myjb.dev.sensor.fingerprint.viewmodel.Status

@BindingAdapter("app:customImageTintList")
fun tintImageResource(view: ImageView, progress: Int) {
    view.imageTintList = ColorStateList.valueOf(Color.argb(255, 255, 255, (100 - progress) * 255 / 100))
}

@BindingAdapter("app:customMessage")
fun setCustomMessage(view: TextView, text: CharSequence) {
    if (TextUtils.isEmpty(text)) {
        return
    }

    view.text = text
}

@BindingAdapter("app:customTextColor")
fun setCustomTextColor(view: TextView, status: Status) {
    val color = when (status) {
        Status.NORMAL -> {
            R.color.hint_color
        }
        Status.ERROR -> {
            R.color.warning_color
        }
        Status.SUCCESS -> {
            R.color.success_color
        }
    }
    view.setTextColor(view.resources.getColor(color))
}

@BindingAdapter("app:customDrawable")
fun setCustomDrawable(view: TextView, status: Status) {
    val drawable = when (status) {
        Status.NORMAL -> {
            R.drawable.fingerprint
        }
        Status.ERROR -> {
            R.drawable.fingerprint_error
        }
        Status.SUCCESS -> {
            R.drawable.fingerprint_success
        }
    }
    view.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0)
}