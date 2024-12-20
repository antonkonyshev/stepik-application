package com.github.antonkonyshev.stepic

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
import com.github.antonkonyshev.stepic.presentation.coursedetails.CourseDetailsScreen
import com.github.antonkonyshev.stepic.presentation.courselist.CourseListScreen
import com.github.antonkonyshev.stepic.presentation.courselist.CourseListViewModel
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
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
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.robolectric.RobolectricTestRunner
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@RunWith(RobolectricTestRunner::class)
class CourseDetailsComposableTestCase : KoinTest {
    private lateinit var context: Context
    private lateinit var courseRepositoryMock: CourseRepository

    @get:Rule
    val detailsRule = createComposeRule()

    @Before
    fun setUp() = runBlocking {
        courseRepositoryMock = Mockito.mock(CourseRepositoryImpl::class.java)
        context = ApplicationProvider.getApplicationContext()
        doReturn(
            listOf(
                Course(
                    12, "Testing title", "Testing summary",
                    "Testing description", "", "", "",
                    9.5f, false, "", Date(), emptyList(),
                    false
                )
            )
        ).`when`(courseRepositoryMock).getNext()
        doReturn(MutableStateFlow("").asStateFlow()).`when`(courseRepositoryMock).searchQuery
        doReturn(MutableStateFlow(false).asStateFlow()).`when`(courseRepositoryMock).ordering
        doNothing().`when`(courseRepositoryMock).clearFilters()
        doReturn(Unit).`when`(courseRepositoryMock).updateBookmark(any())
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
    fun testCourseDetails() = runBlocking {
        val viewModel = CourseListViewModel()
        viewModel.applySearchFilter("")
        with(detailsRule) {
            setContent {
                StepicTheme {
                    CourseDetailsScreen(courseId = 12, viewModel = viewModel)
                }
            }

            assertFalse(viewModel.courses.value.isEmpty())

            onNodeWithTag("favoriteButton").performClick()
            verifyBlocking(courseRepositoryMock, times(1)) {
                updateBookmark(viewModel.courses.value[0])
            }
        }
    }
}