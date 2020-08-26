package com.example.easyfindapp.extensions

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.easyfindapp.R
import com.example.easyfindapp.tools.Tools

fun EditText.emailValidation(check: (Boolean) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            var drawable: Drawable? = null
            if (Tools.emailIsValid(s.toString())) {
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_done)
                check.invoke(true)
            } else {
                check.invoke(false)
            }
            setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                drawable,
                null
            )
        }
    })
}