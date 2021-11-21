package io.keepcoding.chat.common

import android.text.Editable
import android.text.TextWatcher
import android.util.Log

class TextChangedWatcher(private val onTextChanged: (newText: String) -> Unit) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
    override fun afterTextChanged(p0: Editable?) {
        Log.d("RM", "Entra")
    }
    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        charSequence?.toString()?.let(onTextChanged)
    }
}