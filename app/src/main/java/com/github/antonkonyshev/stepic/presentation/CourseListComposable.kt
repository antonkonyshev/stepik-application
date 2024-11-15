package com.github.antonkonyshev.stepic.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.stepic.domain.Course
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import java.util.Date

@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel = viewModel(),
    courses: List<Course> = viewModel.courses.collectAsStateWithLifecycle().value,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(state = listState, modifier = Modifier.padding(12.dp)) {
        items(courses, key = { it.id }) { course ->
            CourseCard(course = course)
        }

        item {
            LoadingSpinner(viewModel.loading.collectAsStateWithLifecycle().value)
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