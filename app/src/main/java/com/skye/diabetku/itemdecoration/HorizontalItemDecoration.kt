package com.skye.diabetku.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecoration (
    private val sideSpacing: Int,
    private val betweenSpacing: Int
) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount ?: 0

            if (position == 0) {
                outRect.left = sideSpacing
            } else {
                outRect.left = betweenSpacing / 1
            }

            if (position == itemCount - 1) {
                outRect.right = sideSpacing
            } else {
                outRect.right = betweenSpacing / 1
            }

        }
}