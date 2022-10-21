package com.armius.dicoding.storyapp.ui.customcomponents

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.armius.dicoding.storyapp.R

class CustomEditText : AppCompatEditText, TextWatcher {
    var validInput : Boolean = false

    constructor(context: Context) : super(context) {
        initialize()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {
        addTextChangedListener(this)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(txtInput: Editable?) {
        if (txtInput!!.isNotBlank() && txtInput.isNotEmpty()) {
            if(inputType == (InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)) {
                if(!Patterns.EMAIL_ADDRESS.matcher(txtInput.toString()).matches()) {
                    validInput = false
                    error = context.getString(R.string.invalid_email_address)
                } else {
                    validInput = true
                }
            } else if(inputType == (InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                if(txtInput.length < 6) {
                    validInput = false
                    error = context.getString(R.string.invalid_password)
                } else {
                    validInput = true
                }
            } else {
                validInput = true
            }
        } else {
            validInput = false
            error = context.getString(R.string.blank_input_text)
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }
}