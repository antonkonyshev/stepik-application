package com.github.antonkonyshev.stepic.presentation.coursedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.stepic.R
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
        CourseDetails(course, modifier = Modifier.padding(bottom = 60.dp))
    }
}

@Composable
fun CourseDetails(course: Course, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        CourseCover(course, coverHeight = 240.dp, detailed = true, cornersRadius = 0.dp)

        Column(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            Text(
                text = course.title, style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 15.dp)
            )

            Card(

            ) {

            }

            val uriHandler = LocalUriHandler.current
            if (course.continue_url.isNotBlank()) {
                Button(
                    onClick = {
                        uriHandler.openUri(course.absoluteContinueUrl())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = stringResource(R.string.start_the_course),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (course.canonical_url.isNotBlank()) {
                Button(
                    onClick = {
                        uriHandler.openUri(course.canonical_url)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text(
                        text = stringResource(R.string.go_to_platform),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = stringResource(R.string.about_the_course),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 15.dp)
            )

            Text(
                text = HtmlCompat.fromHtml(
                    course.description, HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDetailsPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseDetails(
            Course(
                123, "Testing title", "Testing summary",
                "<p>Testing description</p>", "",
                "https://stepik.org/course/1/", "/course/1/continue",
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
                123, "Testing title", "Testing summary",
                "<p>Testing description</p>", "",
                "https://stepik.org/course/1/", "/course/1/continue",
                0.756f, is_paid = false, display_price = "-", create_date = Date(),
                is_favorite = true
            )
        )
    }
}
