package com.github.antonkonyshev.stepic.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.presentation.CourseListScreen
import com.github.antonkonyshev.stepic.presentation.UiEvent
import com.github.antonkonyshev.stepic.presentation.getActivity

sealed class StepicNavRouting(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector
) {
    companion object {
        val screens = listOf(CourseList, CourseDetails, Favorite, Account)

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
        route_course_list, R.string.list_of_courses, Icons.Default.Home
    )

    private object CourseDetails : StepicNavRouting(
        route_course_details, R.string.course, Icons.Default.FolderOpen
    )

    private object Favorite : StepicNavRouting(
        route_favorite, R.string.favorite, Icons.Default.Favorite
    )

    private object Account : StepicNavRouting(
        route_account, R.string.account, Icons.Default.AccountBox
    )
}

@Composable
fun StepicNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = StepicNavRouting.route_course_list,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        composable(StepicNavRouting.route_course_list) {
            CourseListScreen(modifier = modifier)
        }

        composable(
            "${StepicNavRouting.route_course_details}?courseId={courseId}",
            arguments = listOf(navArgument("courseId") { defaultValue = "" })
        ) {
            CourseListScreen(modifier = modifier)
        }

        composable(StepicNavRouting.route_favorite) {
            CourseListScreen(modifier = modifier)
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