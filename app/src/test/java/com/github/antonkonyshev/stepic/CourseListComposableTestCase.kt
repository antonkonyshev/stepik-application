package com.github.antonkonyshev.stepic

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.github.antonkonyshev.stepic.data.repository.CourseRepositoryImpl
import com.github.antonkonyshev.stepic.di.authModule
import com.github.antonkonyshev.stepic.di.databaseModule
import com.github.antonkonyshev.stepic.di.networkModule
import com.github.antonkonyshev.stepic.domain.model.Course
import com.github.antonkonyshev.stepic.domain.repository.CourseRepository
import com.github.antonkonyshev.stepic.presentation.courselist.CourseListScreen
import com.github.antonkonyshev.stepic.presentation.courselist.CourseListViewModel
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.robolectric.RobolectricTestRunner
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CourseListComposableTestCase: KoinTest {
    private lateinit var context: Context
    private lateinit var courseRepositoryMock: CourseRepository

    @get:Rule
    val listRule = createComposeRule()

    @Before
    fun setUp() = runBlocking {
        courseRepositoryMock = Mockito.mock(CourseRepositoryImpl::class.java)
        context = ApplicationProvider.getApplicationContext()
        doReturn(listOf(Course(
            12, "Testing title", "Testing summary",
            "Testing description", "", "", "",
            9.5f, false, "", Date(), emptyList(), false
        ))).`when`(courseRepositoryMock).getNext()
        doReturn(MutableStateFlow("").asStateFlow()).`when`(courseRepositoryMock).searchQuery
        doReturn(MutableStateFlow(false).asStateFlow()).`when`(courseRepositoryMock).ordering
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
                single<CourseRepository> { courseRepositoryMock }
            })
        }
    }

    @After
    fun tearDown() = runBlocking {
        stopKoin()
    }

    @Test
    fun testCoursesList() = runBlocking {
        val viewModel = CourseListViewModel()
        with(listRule) {
            setContent {
                StepicTheme {
                    CourseListScreen(viewModel = viewModel)
                }
            }

            assertEquals(1, viewModel.courses.value.size)
            assertEquals(12, viewModel.courses.value[0].id)

            verify(courseRepositoryMock, times(1)).clearPagination()
            verifyBlocking(courseRepositoryMock, times(1)) { getNext() }

            onNodeWithTag("searchInput").performTextInput("test")
            onNodeWithContentDescription("Search").performClick()

            verify(courseRepositoryMock, times(1)).setSearchQuery("test")
            verify(courseRepositoryMock, times(2)).clearPagination()
            verifyBlocking(courseRepositoryMock, times(2)) { getNext() }

            onNodeWithTag("orderingButton").performClick()

            verify(courseRepositoryMock, times(1)).setOrdering(true)
            verify(courseRepositoryMock, times(3)).clearPagination()
            verifyBlocking(courseRepositoryMock, times(3)) { getNext() }
        }
    }
}