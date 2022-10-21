package com.armius.dicoding.storyapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.armius.dicoding.storyapp.DataDummy
import com.armius.dicoding.storyapp.MainDispatcherRule
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
class AddStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var addStoryViewModel: AddStoryViewModel

    @Mock
    private lateinit var apiInterface: ApiService

    private var dummyImg = MultipartBody.Part.create("dummyFile".toRequestBody())
    private val dummyDesc= "Post description test".toRequestBody("text/plain".toMediaType())
    private val dummyLat = (1.000).toString().toRequestBody("text/plain".toMediaType())
    private val dummyLng = (1.000).toString().toRequestBody("text/plain".toMediaType())

    @Before
    fun setUp() {
        apiInterface = Mockito.mock(ApiService::class.java)
        addStoryViewModel = AddStoryViewModel(apiInterface)
    }

    @Test
    fun test_submit_success() = runTest {
        val mockedCall: Call<ResponseBasic> = Mockito.mock(Call::class.java) as Call<ResponseBasic>

        Mockito.`when`(apiInterface.addNewStory(dummyImg,dummyDesc,dummyLat,dummyLng)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseBasic>
            callback.onResponse(mockedCall, Response.success(DataDummy.responseBasicSuccess()))
            null
        }.`when`(mockedCall).enqueue(ArgumentMatchers.any())

        addStoryViewModel.submitStory(dummyDesc,dummyImg,dummyLat,dummyLng)
        Assert.assertTrue(addStoryViewModel.isSuccess.value==true)
        addStoryViewModel.infoMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                Assert.assertTrue(it=="")
            }
        }
    }

    @Test
    fun test_submit_failed() = runTest {
        val mockedCall: Call<ResponseBasic> = Mockito.mock(Call::class.java) as Call<ResponseBasic>
        val errorResponse =
            "{\n" +
                    "\"error\": true,\n" +
                    "\"message\": \"Error message\"\n" +
                    "}"
        val errorResponseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val expectedResponse = DataDummy.responseBasicFailed()

        Mockito.`when`(apiInterface.addNewStory(dummyImg,dummyDesc,dummyLat,dummyLng)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseBasic>
            callback.onResponse(mockedCall, Response.error(401, errorResponseBody))
            null
        }.`when`(mockedCall).enqueue(ArgumentMatchers.any())

        addStoryViewModel.submitStory(dummyDesc,dummyImg,dummyLat,dummyLng)
        Assert.assertTrue(addStoryViewModel.isSuccess.value==false)
        addStoryViewModel.infoMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                val res = Gson().fromJson(it, ResponseBasic::class.java)
                Assert.assertTrue(res.error==expectedResponse.error)
                Assert.assertTrue(res.message==expectedResponse.message)
            }
        }
    }
}