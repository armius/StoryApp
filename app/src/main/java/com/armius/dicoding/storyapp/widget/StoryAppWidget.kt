package com.armius.dicoding.storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.net.ApiConfig
import com.armius.dicoding.storyapp.core.net.ListStoryItem
import com.armius.dicoding.storyapp.core.net.ResponseAllStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryAppWidget : AppWidgetProvider() {
    companion object {
        private const val TOAST_ACTION = "com.dicoding.picodiploma.TOAST_ACTION"
        const val EXTRA_ITEM = "com.dicoding.picodiploma.EXTRA_ITEM"
        const val WIDGET_DATA_ITEM = "com.armius.dicoding.storyapp.WIDGET_ITEM"
        const val WIDGET_DATA_ITEM_LABEL = "com.armius.dicoding.storyapp.WIDGET_ITEM_LABEL"
        private const val DELAY = 2000L
        private const val PAGE = 1
        private const val SIZE = 25

            private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            GlobalScope.launch(Dispatchers.IO) {
                val tmp = getStoryList(PAGE,SIZE, context)

                while (tmp.value?.size==null) {
                    delay(DELAY)
                }
                val temp = ArrayList<String>()
                val temp2 = ArrayList<String>()
                for (story in tmp.value!!) {
                    temp.add(story.photoUrl)
                    temp2.add(story.name)
                }
                val intent = Intent(context, StackWidgetService::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                intent.putStringArrayListExtra(WIDGET_DATA_ITEM,temp)
                intent.putStringArrayListExtra(WIDGET_DATA_ITEM_LABEL,temp2)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

                val views = RemoteViews(context.packageName, R.layout.story_app_widget)
                views.setRemoteAdapter(R.id.stack_view, intent)
                views.setEmptyView(R.id.stack_view, R.id.empty_view)

                val toastIntent = Intent(context, StoryAppWidget::class.java)
                toastIntent.action = TOAST_ACTION
                toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

                val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    else 0
                )

                views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        private fun getStoryList(page: Int, size: Int, context: Context): LiveData<ArrayList<ListStoryItem>> {
            val liveData = MutableLiveData<ArrayList<ListStoryItem>>()

            val client = ApiConfig.getApiService(context).getAllStories(page, size)
            client.enqueue(object : Callback<ResponseAllStories> {
                override fun onResponse(call: Call<ResponseAllStories>, response: Response<ResponseAllStories>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            liveData.value = responseBody.listStory
                        }
                    } else {
                        Log.e(ContentValues.TAG, "!isSuccessful: $response")
                    }
                }

                override fun onFailure(call: Call<ResponseAllStories>, t: Throwable) {
                  Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                }
            })
            return liveData
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }
}