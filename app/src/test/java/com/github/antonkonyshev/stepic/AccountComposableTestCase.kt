package com.github.antonkonyshev.stepic

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.github.antonkonyshev.stepic.data.repository.AuthRepositoryImpl
import com.github.antonkonyshev.stepic.di.authModule
import com.github.antonkonyshev.stepic.di.databaseModule
import com.github.antonkonyshev.stepic.di.networkModule
import com.github.antonkonyshev.stepic.domain.model.Author
import com.github.antonkonyshev.stepic.domain.repository.AuthRepository
import com.github.antonkonyshev.stepic.presentation.account.AccountScreen
import com.github.antonkonyshev.stepic.presentation.account.AccountViewModel
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
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
import kotlin.test.assertFalse

@RunWith(RobolectricTestRunner::class)
class AccountComposableTestCase : KoinTest {
    private lateinit var context: Context
    private lateinit var authRepositoryMock: AuthRepository

    @get:Rule
    val accountRule = createComposeRule()

    @Before
    fun setUp() = runBlocking {
        authRepositoryMock = Mockito.mock(AuthRepositoryImpl::class.java)
        context = ApplicationProvider.getApplicationContext()
        doReturn(MutableStateFlow(false).asStateFlow()).`when`(authRepositoryMock).authenticated
        doReturn(MutableStateFlow(null).asStateFlow()).`when`(authRepositoryMock).account
        doReturn(Unit).`when`(authRepositoryMock).login(any(), any())
        doReturn(Unit).`when`(authRepositoryMock).loadAccount()
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
                single<AuthRepository> { authRepositoryMock }
            })
        }
    }

    @After
    fun tearDown() = runBlocking {
        stopKoin()
    }

    @Test
    fun testAuthenticationForm() = runBlocking {
        val viewModel = AccountViewModel()
        with(accountRule) {
            setContent {
                StepicTheme {
                    AccountScreen(viewModel = viewModel)
                }
            }

            verifyBlocking(authRepositoryMock, times(0)) { login(any(), any()) }

            onNodeWithTag("loginInput").performTextInput("test@example.com")
            onNodeWithTag("passwordInput").performTextInput("pwd")
            onNodeWithTag("submitButton").performClick()
            delay(100)

            verifyBlocking(authRepositoryMock, times(1)) {
                login("test@example.com", "pwd")
            }
        }
    }

    @Test
    fun testAccountDetailsScreen() = runBlocking {
        doReturn(MutableStateFlow(true).asStateFlow()).`when`(authRepositoryMock).authenticated
        doReturn(
            MutableStateFlow(
                Author(
                    12, "Testing Test", ""
                )
            ).asStateFlow()
        ).`when`(authRepositoryMock).account

        val viewModel = AccountViewModel()
        with(accountRule) {
            setContent {
                StepicTheme {
                    AccountScreen(viewModel = viewModel)
                }
            }

            onNodeWithText("Testing Test").assertIsDisplayed()
            assertFalse(viewModel.loading.value)
        }
    }
}