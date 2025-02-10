package com.letthemcook.theme.components.dialogs.bottom.writeReview

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class ReviewWriter {

    val isDialogShown = mutableStateOf(false)
    val showEmptyTextError = mutableStateOf(false)

    val reviewTextState = TextFieldState()

    private var onSendReviewCallback: MutableState<((String) -> Unit)?> = mutableStateOf(null)
    private var onDismiss: MutableState<(() -> Unit)?> = mutableStateOf(null)

    fun showDialog(
        onSendReviewCallback: ((String) -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        this.onSendReviewCallback.value = onSendReviewCallback
        this.onDismiss.value = onDismiss

        isDialogShown.value = true
    }

    fun hideDialog() {
        onSendReviewCallback.value = null

        onDismiss.value?.invoke()
        onDismiss.value = null

        isDialogShown.value = false
        reviewTextState.clearText()
    }

    fun hideDialogAndSendReview() {
        if (reviewTextState.text.isEmpty()) {
            showEmptyTextError.value = true
            return
        } else {
            showEmptyTextError.value = false
        }

        onSendReviewCallback.value?.invoke(reviewTextState.text.toString())
        hideDialog()
    }
}