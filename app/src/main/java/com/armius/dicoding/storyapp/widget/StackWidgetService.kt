package com.armius.dicoding.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.armius.dicoding.storyapp.widget.StoryAppWidget.Companion.WIDGET_DATA_ITEM
import com.armius.dicoding.storyapp.widget.StoryAppWidget.Companion.WIDGET_DATA_ITEM_LABEL

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val tmp = intent.getStringArrayListExtra(WIDGET_DATA_ITEM) as ArrayList<String>
        val tmp2 = intent.getStringArrayListExtra(WIDGET_DATA_ITEM_LABEL) as ArrayList<String>
        return StackRemoteViewsFactory(this.applicationContext, tmp, tmp2)
    }
}