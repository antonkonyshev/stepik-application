package com.github.antonkonyshev.stepic

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.data.network.UsersResponse
import com.github.antonkonyshev.stepic.data.repository.AuthRepositoryImpl
import com.github.antonkonyshev.stepic.di.authModule
import com.github.antonkonyshev.stepic.di.databaseModule
import com.github.antonkonyshev.stepic.di.networkModule
import com.github.antonkonyshev.stepic.domain.model.Author
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.declare
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTestCase : KoinTest {
    private lateinit var apiMock: StepicApi
    private lateinit var context: Context
    private val account = Author(12, "Testing Test", "https://localhost/avatar.svg")

    @Before
    fun setUp() = runBlocking {
        apiMock = Mockito.mock(StepicApi::class.java)
        context = ApplicationProvider.getApplicationContext()
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger()
                androidContext(context)
                modules(databaseModule, networkModule, authModule)
                allowOverride(true)
            }
        }
        declare {
            loadKoinModules(module {
                single<StepicApi> { apiMock }
            })
        }
    }

    @After
    fun tearDown() = runBlocking {
        stopKoin()
    }

    @Test
    fun testLoginSuccessful() = runBlocking {
        val mockResponse = Mockito.mock(Response::class.java)
        doReturn(204).`when`(mockResponse).code()
        doReturn(mockResponse).`when`(apiMock).login(any())
        doReturn(Unit).`when`(apiMock).loadAccount(any())

        verifyBlocking(apiMock, times(0)) { loadAccount(any()) }

        val repository = AuthRepositoryImpl()
        repository.login("test@example.com", "pwd")
        verifyBlocking(apiMock, times(1)) { loadAccount(1) }
        assertTrue(repository.authenticated.value)
    }

    @Test
    fun testLoginFailed() = runBlocking {
        val mockResponse = Mockito.mock(Response::class.java)
        doReturn(403).`when`(mockResponse).code()
        doReturn(mockResponse).`when`(apiMock).login(any())
        doReturn(Unit).`when`(apiMock).loadAccount(any())

        val repository = AuthRepositoryImpl()
        verifyBlocking(apiMock, times(0)) { loadAccount(any()) }
        repository.login("test@example.com", "pwd")
        verifyBlocking(apiMock, times(0)) { loadAccount(any()) }
        assertFalse(repository.authenticated.value)
    }

    @Test
    fun testLoadAccount() = runBlocking {
        val author = Author(12, "Testing Test", "https://localhost/avatar.svg")
        doReturn(UsersResponse(users = listOf(author))).`when`(apiMock).loadAccount(1)

        val repository = AuthRepositoryImpl()
        verifyBlocking(apiMock, times(0)) { loadAccount(any()) }
        repository.loadAccount()
        verifyBlocking(apiMock, times(1)) { loadAccount(any()) }
        assertEquals(author, repository.account.value)
    }
}