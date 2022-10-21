package com.armius.dicoding.storyapp.core.paging

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.armius.dicoding.storyapp.core.database.StoryDatabase
import com.armius.dicoding.storyapp.core.entity.StoryEntity
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.ui.home.StoryListAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRepositoryTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    private lateinit var storyRepository: StoryRepository

    @Before
    fun setUp() {
        storyRepository = StoryRepository(mockDb, mockApi)
    }

    @Test
    fun getStory() = runTest {
        val actualStory: LiveData<PagingData<StoryEntity>> = storyRepository.getStory()
        Handler(Looper.getMainLooper()).post {
            actualStory.observeForever {
                val differ = AsyncPagingDataDiffer(
                    diffCallback = StoryListAdapter.DIFF_CALLBACK,
                    updateCallback = noopListUpdateCallback,
                    workerDispatcher = Dispatchers.Main,
                )
                GlobalScope.launch(Dispatchers.IO) {
                    differ.submitData(it)
                    Assert.assertNotNull(differ.snapshot())
                }
            }
        }
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}