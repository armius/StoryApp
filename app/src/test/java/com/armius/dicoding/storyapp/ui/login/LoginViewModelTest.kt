package com.armius.dicoding.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.armius.dicoding.storyapp.*
import com.armius.dicoding.storyapp.core.net.ApiService
import com.armius.dicoding.storyapp.core.net.ResponseBasic
import com.armius.dicoding.storyapp.core.net.ResponseLogin
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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var loginViewModel: LoginViewModel

    @Mock
    private lateinit var apiInterface: ApiService

    private val dummyEmail = "submissiontesting@gmail.com"
    private val dummyPassword = "123123"

    @Before
    fun setUp() {
        apiInterface = mock(ApiService::class.java)
        loginViewModel = LoginViewModel(apiInterface)
    }

    @Test
    fun test_login_success() = runTest {
        val mockedCall: Call<ResponseLogin> = mock(Call::class.java) as Call<ResponseLogin>
        val expectedResponse = DataDummy.loginResponseSuccess()

        Mockito.`when`(apiInterface.loginUser(dummyEmail, dummyPassword)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseLogin>
            callback.onResponse(mockedCall, Response.success(DataDummy.loginResponseSuccess()))
            null
        }.`when`(mockedCall).enqueue(any())

        loginViewModel.loginUser(dummyEmail, dummyPassword)
        Assert.assertTrue(loginViewModel.isLoginSuccess.value==true)
        Assert.assertTrue(loginViewModel.token.value==expectedResponse.loginResult.token)
        Assert.assertTrue(loginViewModel.userID.value==expectedResponse.loginResult.userId)
        Assert.assertTrue(loginViewModel.userName.value==expectedResponse.loginResult.name)
        loginViewModel.errMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                Assert.assertTrue(it=="")
            }
        }
    }

    @Test
    fun test_login_failed() = runTest {
        val mockedCall: Call<ResponseLogin> = mock(Call::class.java) as Call<ResponseLogin>
        val errorResponse =
            "{\n" +
                "\"error\": true,\n" +
                "\"message\": \"Invalid password/ user not found\"\n" +
            "}"
        val errorResponseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val expectedResponse = DataDummy.loginResponseFailed()

        Mockito.`when`(apiInterface.loginUser(dummyEmail, dummyPassword)).thenReturn(mockedCall)
        doAnswer {
            val callback = it.arguments[0] as Callback<ResponseLogin>

            callback.onResponse(mockedCall, Response.error(401, errorResponseBody))
            null
        }.`when`(mockedCall).enqueue(any())

        loginViewModel.loginUser(dummyEmail, dummyPassword)
        Assert.assertTrue(loginViewModel.isLoginSuccess.value==false)
        Assert.assertNull(loginViewModel.token.value)
        Assert.assertNull(loginViewModel.userID.value)
        Assert.assertNull(loginViewModel.userName.value)
        loginViewModel.errMsg.observeForever {
            it.getContentIfNotHandled()?.let {
                val res = Gson().fromJson(it, ResponseBasic::class.java)
                Assert.assertTrue(res.error==expectedResponse.error)
                Assert.assertTrue(res.message==expectedResponse.message)
            }
        }
    }
}