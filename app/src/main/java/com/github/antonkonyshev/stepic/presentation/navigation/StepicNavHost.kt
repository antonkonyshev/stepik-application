package com.github.antonkonyshev.stepic.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.presentation.CourseListScreen
import com.github.antonkonyshev.stepic.presentation.CourseListViewModel
import com.github.antonkonyshev.stepic.presentation.UiEvent
import com.github.antonkonyshev.stepic.presentation.getActivity

sealed class StepicNavRouting(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector
) {
    companion object {
        val screens = listOf(CourseList, Favorite, Account)

        const val route_course_list = "courses"
        const val route_course_details = "course"
        const val route_favorite = "favorite"
        const val route_account = "account"

        fun navigationUiEvent(route: String): UiEvent {
            return UiEvent("NavigateTo", route)
        }

        fun courseDetailsNavigationUiEvent(courseId: Long): UiEvent {
            return UiEvent("NavigateTo", "${route_course_details}?courseId=${courseId}")
        }
    }

    private object CourseList : StepicNavRouting(
        route_course_list, R.string.main, Icons.Outlined.Home
    )

    private object CourseDetails : StepicNavRouting(
        route_course_details, R.string.course, Icons.Outlined.Info
    )

    private object Favorite : StepicNavRouting(
        route_favorite, R.string.favorite, Icons.Outlined.BookmarkBorder
    )

    private object Account : StepicNavRouting(
        route_account, R.string.account, Icons.Outlined.Person
    )
}

@Composable
fun StepicNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val courseListViewModel: CourseListViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = StepicNavRouting.route_course_list,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        composable(StepicNavRouting.route_course_list) {
            CourseListScreen(viewModel = courseListViewModel, modifier = modifier)
        }

        composable(
            "${StepicNavRouting.route_course_details}?courseId={courseId}",
            arguments = listOf(navArgument("courseId") { defaultValue = "" })
        ) {
            CourseListScreen(modifier = modifier)
        }

        composable(StepicNavRouting.route_favorite) {
            CourseListScreen(viewModel = courseListViewModel, favorite = true, modifier = modifier)
        }

        composable(StepicNavRouting.route_account) {
            CourseListScreen(modifier = modifier)
        }
    }

    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        ctx.getActivity()?.eventBus?.collect {
            if (it.id == "NavigateTo") {
                navController.navigate(it.extra)
            }
        }
    }
}