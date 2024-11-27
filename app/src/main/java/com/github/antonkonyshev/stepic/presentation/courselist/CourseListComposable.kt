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
import androidx.compose.foundation.lazy.LazyListState
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
import com.github.antonkonyshev.stepic.domain.model.Course
import com.github.antonkonyshev.stepic.presentation.getActivity
import com.github.antonkonyshev.stepic.presentation.navigation.StepicNavRouting
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel = viewModel(),
    favorite: Boolean = false,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(favorite) {
        viewModel.changeScreen(favorite)
    }

    val ctx = LocalContext.current
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

        CourseList(
            courses = viewModel.courses.collectAsStateWithLifecycle().value,
            listState = listState,
            toggleFavorite = viewModel::toggleFavorite,
            loading = viewModel.loading,
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collect {
            if (it != null && it >= viewModel.courses.value.size - 2) {
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
fun CourseList(
    courses: List<Course>,
    loading: StateFlow<Boolean>,
    listState: LazyListState = rememberLazyListState(),
    toggleFavorite: (Course) -> Unit = {},
) {
    val ctx = LocalContext.current
    LazyColumn(state = listState, modifier = Modifier) {
        items(courses, key = { it.id }) { course ->
            CourseCard(
                course = course,
                toggleFavorite = toggleFavorite,
                navigateToCourseDetails = { course: Course ->
                    ctx.getActivity()?.emitUiEvent(
                        StepicNavRouting.courseDetailsNavigationUiEvent(course.id)
                    )
                }
            )
        }

        item {
            LoadingSpinner(loading.collectAsStateWithLifecycle().value)
        }
    }
}

@Composable
fun LoadingSpinner(loading: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = loading,
        enter = fadeIn(animationSpec = TweenSpec(durationMillis = 200)),
        exit = fadeOut(animationSpec = TweenSpec(durationMillis = 200)),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseListPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseList(
            courses = listOf(
                Course(
                    1, "Test 1", "Testing course",
                    "Testing description",
                    "https://cdn.stepik.net/media/cache/images/courses/221585/cover_EJp9uXA/e39ef81b8985462b5f92f9e3a41e9afb.jpg",
                    canonical_url = "https://stepik.org/course/1",
                    continue_url = "/course/1/continue",
                    readiness = 0.89f,
                    is_paid = true,
                    display_price = "15000 ₽",
                    create_date = Date(),
                    authors = listOf(123L),
                    is_favorite = false,
                ),
                Course(
                    2, "Test 2", "Testing course",
                    "Testing description",
                    "https://cdn.stepik.net/media/cache/images/courses/221585/cover_EJp9uXA/e39ef81b8985462b5f92f9e3a41e9afb.jpg",
                    canonical_url = "https://stepik.org/course/1",
                    continue_url = "/course/1/continue",
                    readiness = 0.91f,
                    is_paid = false,
                    display_price = "-",
                    create_date = Date(),
                    authors = listOf(123L),
                    is_favorite = true,
                ),
                Course(
                    3, "Test 1", "Testing course",
                    "Testing description",
                    "https://cdn.stepik.net/media/cache/images/courses/221585/cover_EJp9uXA/e39ef81b8985462b5f92f9e3a41e9afb.jpg",
                    canonical_url = "https://stepik.org/course/1",
                    continue_url = "/course/1/continue",
                    readiness = 0.75f,
                    is_paid = false,
                    display_price = "-",
                    create_date = Date(),
                    authors = emptyList(),
                    is_favorite = false,
                )
            ),
            loading = MutableStateFlow(false),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingSpinnerPreview() {
    LoadingSpinner(true)
}