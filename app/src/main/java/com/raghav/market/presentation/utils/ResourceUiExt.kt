package com.raghav.market.presentation.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackBarHelper {
    companion object {
        var isShowing = false

        fun showSnackbarAction(view: View, actionNameRes: Int, actionMessageRes: Int, action: () -> Any) {
            if (isShowing) return
            val snackBar: Snackbar = Snackbar.make(view, actionMessageRes, Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction(actionNameRes) {
                action.invoke()
                isShowing = false
            }.show()
            isShowing = true
        }
    }
}

fun String.Companion.formatResource(context: Context, resId: Int, vararg values: Any?): CharSequence {
    val strResource = context.resources.getString(resId)
    return String.format(strResource, *values)
}