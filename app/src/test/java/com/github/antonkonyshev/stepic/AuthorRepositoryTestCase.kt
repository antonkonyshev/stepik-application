package com.github.antonkonyshev.stepic

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.antonkonyshev.stepic.data.database.AuthorDao
import com.github.antonkonyshev.stepic.data.database.AuthorData
import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.data.network.UsersResponse
import com.github.antonkonyshev.stepic.data.repository.AuthorRepositoryImpl
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
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AuthorRepositoryTestCase : KoinTest {
    private lateinit var apiMock: StepicApi
    private lateinit var daoMock: AuthorDao
    private lateinit var context: Context
    private val author = Author(12, "Testing Test", "https://localhost/avatar.svg")

    @Before
    fun setUp() = runBlocking {
        apiMock = Mockito.mock(StepicApi::class.java)
        daoMock = Mockito.mock(AuthorDao::class.java)
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
                single<AuthorDao> { daoMock }
            })
        }
    }

    @After
    fun tearDown() = runBlocking {
        stopKoin()
    }

    @Test
    fun testGetAuthorByIdFromDatabase() = runBlocking {
        doReturn(AuthorData(author.id, author.full_name, author.avatar)).`when`(daoMock)
            .getAuthorById(12)
        doReturn(null).`when`(apiMock).fetchUser(any())

        val repository = AuthorRepositoryImpl()
        assertEquals(author, repository.getAuthorById(12))
    }

    @Test
    fun testGetAuthorByIdFromNetwork() = runBlocking {
        doReturn(null).`when`(daoMock).getAuthorById(any())
        doReturn(Unit).`when`(daoMock).upsertAuthor(any())
        doReturn(
            UsersResponse(
                listOf(author)
            )
        ).`when`(apiMock).fetchUser(12)

        val repository = AuthorRepositoryImpl()
        assertEquals(author, repository.getAuthorById(12))
    }
}