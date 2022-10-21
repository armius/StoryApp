package com.armius.dicoding.storyapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.armius.dicoding.storyapp.R
import com.bumptech.glide.Glide

class StackRemoteViewsFactory(private val mContext: Context, private val dataList: ArrayList<String>, private val dataLabelList: ArrayList<String>) : RemoteViewsService.RemoteViewsFactory {
    private val mWidgetItems = ArrayList<String>()
    private val mWidgetItems2 = ArrayList<String>()

    override fun onDataSetChanged() {
        for (tmp in dataList) {
            mWidgetItems.add(tmp)
        }
        for (tmp in dataLabelList) {
            mWidgetItems2.add(tmp)
        }
    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        val extras = bundleOf(
            StoryAppWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        val bitmap = Glide.with(mContext)
            .asBitmap()
            .load(mWidgetItems[position])
            .submit(512, 512)
            .get()
        rv.setImageViewBitmap(R.id.imageView, bitmap)
        rv.setTextViewText(R.id.tvWidget,mWidgetItems2[position])
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}