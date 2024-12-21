package com.github.antonkonyshev.stepic

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.antonkonyshev.stepic.data.database.CourseDao
import com.github.antonkonyshev.stepic.data.network.CourseResponse
import com.github.antonkonyshev.stepic.data.network.Meta
import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.data.repository.CourseRepositoryImpl
import com.github.antonkonyshev.stepic.di.authModule
import com.github.antonkonyshev.stepic.di.databaseModule
import com.github.antonkonyshev.stepic.di.networkModule
import com.github.antonkonyshev.stepic.domain.model.Course
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
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyBlocking
import org.robolectric.RobolectricTestRunner
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CourseRepositoryTestCase : KoinTest {
    private lateinit var apiMock: StepicApi
    private lateinit var daoMock: CourseDao
    private lateinit var context: Context

    private val course1 = Course(
        1, "Testing title", "Testing summary",
        "Testing description", "", "", "",
        9.5f, false, "", Date(), listOf(1), false
    )
    private val course2 = Course(
        2, "Another title", "Another summary",
        "Another description", "", "", "",
        9.3f, false, "", Date(), listOf(2), false
    )
    private val course3 = Course(
        3, "Third title", "Third summary",
        "Third description", "", "", "",
        8.8f, false, "", Date(), listOf(1), false
    )

    @Before
    fun setUp() = runBlocking {
        apiMock = Mockito.mock(StepicApi::class.java)
        daoMock = Mockito.mock(CourseDao::class.java)
        context = ApplicationProvider.getApplicationContext()

        doReturn(false).`when`(daoMock).getFavoriteAttribute(any())
        doReturn(Unit).`when`(daoMock).upsertCourses()

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
                single<CourseDao> { daoMock }
            })
        }
    }

    @After
    fun tearDown() = runBlocking {
        stopKoin()
    }

    @Test
    fun testGetNext() = runBlocking {
        doReturn(
            CourseResponse(listOf(course1, course2), Meta(1L, true, false))
        ).`when`(apiMock).fetchCourses(1L, 20, null, null)

        val repository = CourseRepositoryImpl()
        verifyBlocking(apiMock, times(0)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        var resCourses = repository.getNext()
        verifyBlocking(apiMock, times(1)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        assertEquals(2, resCourses.size)
        assertEquals(course1, resCourses[0])
        assertEquals(course2, resCourses[1])
        assertTrue(repository.hasNext)
        assertFalse(repository.hasPrevious)

        doReturn(
            CourseResponse(listOf(), Meta(2L, true, true))
        ).`when`(apiMock).fetchCourses(2L, 20, null, null)
        doReturn(
            CourseResponse(listOf(course3), Meta(3L, false, true))
        ).`when`(apiMock).fetchCourses(3L, 20, null, null)
        resCourses = repository.getNext()
        verifyBlocking(apiMock, times(3)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        assertEquals(1, resCourses.size)
        assertEquals(course3, resCourses[0])
        assertFalse(repository.hasNext)
        assertTrue(repository.hasPrevious)
    }

    @Test
    fun testGetPrevious() = runBlocking {
        val repository = CourseRepositoryImpl()
        repository.page = 4
        doReturn(
            CourseResponse(listOf(course3), Meta(3L, false, true))
        ).`when`(apiMock).fetchCourses(3L, 20, null, null)

        verifyBlocking(apiMock, times(0)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        var resCourses = repository.getPrevious()
        verifyBlocking(apiMock, times(1)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        assertEquals(1, resCourses.size)
        assertEquals(course3, resCourses[0])
        assertFalse(repository.hasNext)
        assertTrue(repository.hasPrevious)

        doReturn(
            CourseResponse(listOf(), Meta(2L, true, true))
        ).`when`(apiMock).fetchCourses(2L, 20, null, null)
        doReturn(
            CourseResponse(listOf(course1, course2), Meta(1L, true, false))
        ).`when`(apiMock).fetchCourses(1L, 20, null, null)
        resCourses = repository.getPrevious()
        verifyBlocking(apiMock, times(3)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        assertEquals(2, resCourses.size)
        assertEquals(course1, resCourses[0])
        assertEquals(course2, resCourses[1])
        assertTrue(repository.hasNext)
        assertFalse(repository.hasPrevious)
    }

    @Test
    fun testSearchAndOrdering() = runBlocking {
        doReturn(
            CourseResponse(listOf(course1, course2), Meta(1L, true, false))
        ).`when`(apiMock).fetchCourses(1L, 20, "test", "create_date")

        val repository = CourseRepositoryImpl()
        verifyBlocking(apiMock, times(0)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        repository.setSearchQuery("test")
        repository.setOrdering(true)
        var resCourses = repository.getNext()
        verifyBlocking(apiMock, times(1)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        assertEquals(2, resCourses.size)
        assertEquals(course1, resCourses[0])
        assertEquals(course2, resCourses[1])

        repository.clearFilters()
        repository.clearPagination()
        doReturn(
            CourseResponse(listOf(course3), Meta(1L, true, false))
        ).`when`(apiMock).fetchCourses(1L, 20, null, null)
        resCourses = repository.getNext()
        verifyBlocking(apiMock, times(2)) { fetchCourses(any(), any(), anyOrNull(), anyOrNull()) }
        assertEquals(1, resCourses.size)
        assertEquals(course3, resCourses[0])
    }

    @Test
    fun testUpdateBookmark() = runBlocking {
        course1.is_favorite = true
        doReturn(Unit).`when`(daoMock).setFavorite(course1.id, course1.is_favorite)
        doReturn(Unit).`when`(apiMock).addBookmark(any())
        doReturn(Unit).`when`(apiMock).deleteBookmark(any())

        val repository = CourseRepositoryImpl()
        verifyBlocking(daoMock, times(0)) { setFavorite(course1.id, true) }
        verifyBlocking(apiMock, times(0)) { addBookmark(any()) }
        verifyBlocking(apiMock, times(0)) { deleteBookmark(any()) }
        repository.updateBookmark(course1)
        verifyBlocking(daoMock, times(1)) { setFavorite(course1.id, true) }
        verifyBlocking(apiMock, times(1)) { addBookmark(any()) }
        verifyBlocking(apiMock, times(0)) { deleteBookmark(any()) }

        course1.is_favorite = false
        repository.updateBookmark(course1)
        verifyBlocking(daoMock, times(1)) { setFavorite(course1.id, false) }
        verifyBlocking(apiMock, times(1)) { addBookmark(any()) }
        verifyBlocking(apiMock, times(1)) { deleteBookmark(any()) }

    }
}