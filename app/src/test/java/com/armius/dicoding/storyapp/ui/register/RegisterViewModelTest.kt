package com.armius.dicoding.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.armius.dicoding.storyapp.DataDummy
import com.armius.dicoding.storyapp.MainDispatcherRule
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.armius.dicoding.storyapp.ui.utils.Event
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
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var registerViewModel: RegisterViewModel

    @Mock
    private lateinit var apiInterface: ApiService

    private val dummyName = "submissiontesting"
    private val dummyEmail = "submissiontesting@gmail.com"
    private val dummyPassword = "123123"

    @Before
    fun setUp() {
        apiInterface = Mockito.mock(ApiService::class.java)
        registerViewModel = RegisterViewModel(apiInterface)
    }

    @Test
    fun test_register_success() = runTest {
        val mockedCall: Call<ResponseBasic> = Mockito.mock(Call::class.java) as Call<ResponseBasic>

        Mockito.`when`(apiInterface.registerUser(dummyName,dummyEmail,dummyPassword)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseBasic>
            callback.onResponse(mockedCall, Response.success(DataDummy.responseBasicSuccess()))
            null
        }.`when`(mockedCall).enqueue(ArgumentMatchers.any())

        registerViewModel.registerUser(dummyName,dummyEmail,dummyPassword)
        registerViewModel.infoMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                Assert.assertTrue(it===Event.REGISTER_SUCCESS)
            }
        }
    }

    @Test
    fun test_register_failed() = runTest {
        val mockedCall: Call<ResponseBasic> = Mockito.mock(Call::class.java) as Call<ResponseBasic>
        val errorResponse =
            "{\n" +
                "\"error\": true,\n" +
                "\"message\": \"Error message\"\n" +
            "}"
        val errorResponseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val expectedResponse = DataDummy.responseBasicFailed()

        Mockito.`when`(apiInterface.registerUser(dummyName,dummyEmail,dummyPassword)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseBasic>
            callback.onResponse(mockedCall, Response.error(401, errorResponseBody))
            null
        }.`when`(mockedCall).enqueue(ArgumentMatchers.any())

        registerViewModel.registerUser(dummyName,dummyEmail,dummyPassword)
        registerViewModel.infoMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                val res = Gson().fromJson(it, ResponseBasic::class.java)
                Assert.assertTrue(res.error==expectedResponse.error)
                Assert.assertTrue(res.message==expectedResponse.message)
            }
        }
    }
}