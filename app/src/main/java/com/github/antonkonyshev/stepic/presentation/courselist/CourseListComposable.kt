package com.github.antonkonyshev.stepic.presentation.courselist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.domain.Course
import com.github.antonkonyshev.stepic.presentation.getActivity
import com.github.antonkonyshev.stepic.presentation.navigation.StepicNavRouting
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import java.util.Date

@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel = viewModel(),
    courses: List<Course> = viewModel.courses.collectAsStateWithLifecycle().value,
    favorite: Boolean = false,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(favorite) {
        viewModel.changeScreen(favorite)
        viewModel.selectCourse(null)
    }

    val listState = rememberLazyListState()

    Column {
        AnimatedVisibility(visible = !favorite) {
            CourseListFiltering(
                searchQuery = viewModel.courseRepository.searchQuery.collectAsStateWithLifecycle().value,
                ordering = viewModel.courseRepository.ordering.collectAsStateWithLifecycle().value,
                onSearch = viewModel::applySearchFilter,
                toggleOrdering = viewModel::toggleOrdering
            )
        }

        AnimatedVisibility(visible = favorite) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .height(60.dp)
                    .padding(start = 15.dp, top = 35.dp, end = 15.dp, bottom = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.favorite),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }

        val ctx = LocalContext.current
        LazyColumn(state = listState, modifier = Modifier) {
            items(courses, key = { it.id }) { course ->
                CourseCard(
                    course = course,
                    toggleFavorite = viewModel::toggleFavorite,
                    navigateToCourseDetails = { course: Course ->
                        viewModel.selectCourse(course)
                        ctx.getActivity()?.emitUiEvent(
                            StepicNavRouting.courseDetailsNavigationUiEvent(course.id)
                        )
                    }
                )
            }

            item {
                LoadingSpinner(viewModel.loading.collectAsStateWithLifecycle().value)
            }
        }
    }

    val ctx = LocalContext.current
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collect {
            if (it != null && it >= courses.size - 2) {
                viewModel.loadFurther(
                    viewModel::loadNext, listState, ctx.getActivity()?.lifecycleScope
                )
            }
        }
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }.collect {
            if (it != null && it <= 0) {
                viewModel.loadFurther(
                    viewModel::loadPrevious, listState, ctx.getActivity()?.lifecycleScope
                )
            }
        }
    }
}

@Composable
fun LoadingSpinner(loading: Boolean) {
    AnimatedVisibility(
        visible = loading,
        enter = fadeIn(animationSpec = TweenSpec(durationMillis = 200)),
        exit = fadeOut(animationSpec = TweenSpec(durationMillis = 200))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun CourseListScreenPreview() {
    val viewModel = CourseListViewModel()
    StepicTheme(darkTheme = true) {
        CourseListScreen(
            viewModel = viewModel,
            courses = listOf(
                Course(
                    1, "Test 1", "Testing course",
                    "https://cdn.stepik.net/media/cache/images/courses/221585/cover_EJp9uXA/e39ef81b8985462b5f92f9e3a41e9afb.jpg",
                    readiness = 0.89f,
                    is_paid = false,
                    display_price = "-",
                    create_date = Date()
                ),
                Course(
                    1, "Test 2", "Testing course",
                    "https://cdn.stepik.net/media/cache/images/courses/221585/cover_EJp9uXA/e39ef81b8985462b5f92f9e3a41e9afb.jpg",
                    readiness = 0.91f,
                    is_paid = false,
                    display_price = "-",
                    create_date = Date()
                ),
                Course(
                    1, "Test 1", "Testing course",
                    "https://cdn.stepik.net/media/cache/images/courses/221585/cover_EJp9uXA/e39ef81b8985462b5f92f9e3a41e9afb.jpg",
                    readiness = 0.75f,
                    is_paid = false,
                    display_price = "-",
                    create_date = Date()
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingSpinnerPreview() {
    LoadingSpinner(true)
}