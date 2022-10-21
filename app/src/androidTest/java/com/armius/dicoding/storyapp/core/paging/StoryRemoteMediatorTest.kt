package com.armius.dicoding.storyapp.core.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.armius.dicoding.storyapp.core.database.StoryDatabase
import com.armius.dicoding.storyapp.core.entity.ResponseAllStoriesEntity
import com.armius.dicoding.storyapp.core.entity.StoryEntity
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseAllStories
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.armius.dicoding.storyapp.core.net.ResponseLogin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest{

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, StoryEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override fun registerUser(name: String, email: String, password: String): Call<ResponseBasic> {
        TODO("Not yet implemented")
    }

    override fun loginUser(email: String, password: String): Call<ResponseLogin> {
        TODO("Not yet implemented")
    }

    override fun getAllStories(page: Int?, size: Int?, location: Int?): Call<ResponseAllStories> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllStories2(
        page: Int?,
        size: Int?,
        location: Int?
    ): ResponseAllStoriesEntity {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                "name $i",
                "description $i",
                "http://imgdummy.com/photo$i.jpg",
                "today $i",
                1.0,
                1.0,
            )
            items.add(quote)
        }
        if (page != null) {
            return ResponseAllStoriesEntity(items.subList((page - 1) * size!!, (page - 1) * size + size), false, "")
        }
        return ResponseAllStoriesEntity(items, false, "")
    }

    override fun addNewStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Call<ResponseBasic> {
        TODO("Not yet implemented")
    }
}