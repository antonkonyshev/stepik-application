package com.github.antonkonyshev.stepic.presentation.coursedetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.stepic.domain.Course
import com.github.antonkonyshev.stepic.presentation.courselist.CourseCover
import com.github.antonkonyshev.stepic.presentation.courselist.CourseListViewModel
import com.github.antonkonyshev.stepic.presentation.getActivity
import com.github.antonkonyshev.stepic.presentation.navigation.StepicNavRouting
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import java.util.Date

@Composable
fun CourseDetailsScreen(
    viewModel: CourseListViewModel = viewModel(),
    courseId: Long,
    modifier: Modifier = Modifier
) {
    val course = remember(courseId) { viewModel.courses.value.firstOrNull { it.id == courseId } }

    if (course == null) {
        // TODO: Check id and load from API
        LocalContext.current.getActivity()?.emitUiEvent(
            "NavigateTo", StepicNavRouting.route_course_list
        )
    } else {
        CourseDetails(course)
    }
}

@Composable
fun CourseDetails(course: Course) {
    CourseCover(course, coverHeight = 240.dp, detailed = true)
}

@Preview(showBackground = true)
@Composable
fun CourseDetailsPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseDetails(
            Course(
                123, "Testing title", "Testing summary", "",
                0.89123f, is_paid = true, display_price = "15000 ₽", create_date = Date(),
                is_favorite = false
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDetailsOfFreeAndFavoritePreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseDetails(
            Course(
                123, "Testing title", "Testing summary", "",
                0.756f, is_paid = false, display_price = "-", create_date = Date(),
                is_favorite = true
            )
        )
    }
}
