package com.armius.dicoding.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.armius.dicoding.storyapp.DataDummy
import com.armius.dicoding.storyapp.MainDispatcherRule
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseAllStories
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mapsViewModel: MapsViewModel

    @Mock
    private lateinit var apiInterface: ApiService

    private val page = 1
    private val size = 50

    @Before
    fun setUp() {
        apiInterface = Mockito.mock(ApiService::class.java)
        mapsViewModel = MapsViewModel(apiInterface)
    }

    @Test
    fun test_get_story_success() = runTest {
        val mockedCall: Call<ResponseAllStories> = Mockito.mock(Call::class.java) as Call<ResponseAllStories>

        Mockito.`when`(apiInterface.getAllStories(page,size,1)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseAllStories>
            callback.onResponse(mockedCall, Response.success(DataDummy.generateDummyStoryResponse2("success")))
            null
        }.`when`(mockedCall).enqueue(ArgumentMatchers.any())

        mapsViewModel.getStoryList(page,size)
        mapsViewModel.infoMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                Assert.assertTrue(it== "")
            }
        }
        mapsViewModel.listStory.observeForever {
            Assert.assertTrue(it.isNotEmpty())
        }
    }

    @Test
    fun test_get_story_failed() = runTest {
        val mockedCall: Call<ResponseAllStories> = Mockito.mock(Call::class.java) as Call<ResponseAllStories>
        val errorResponse =
            "{\n" +
                    "\"error\": true,\n" +
                    "\"message\": \"Error message\"\n" +
                    "}"
        val errorResponseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val expectedResponse = DataDummy.responseBasicFailed()

        Mockito.`when`(apiInterface.getAllStories(page,size,1)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseAllStories>
            callback.onResponse(mockedCall, Response.error(401, errorResponseBody))
            null
        }.`when`(mockedCall).enqueue(ArgumentMatchers.any())

        mapsViewModel.getStoryList(page,size)
        mapsViewModel.infoMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                val res = Gson().fromJson(it, ResponseBasic::class.java)
                Assert.assertTrue(res.error==expectedResponse.error)
                Assert.assertTrue(res.message==expectedResponse.message)
            }
        }
        mapsViewModel.listStory.observeForever {
            Assert.assertTrue(it.isEmpty())
        }
    }
}