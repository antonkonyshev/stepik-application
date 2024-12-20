package com.github.antonkonyshev.stepic.presentation.courselist

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.domain.model.Course
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

@Composable
fun CourseCard(
    course: Course,
    toggleFavorite: (Course) -> Unit = {},
    navigateToCourseDetails: (Course) -> Unit = {},
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier.padding(15.dp)
    ) {
        val textPaddingsModifier = remember {
            Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        }

        CourseCover(
            course = course,
            toggleFavorite = toggleFavorite,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
        )

        Text(
            text = course.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = textPaddingsModifier
        )

        AnimatedVisibility(
            visible = course.summary.isNotBlank() &&
                    course.summary.contains("[A-zА-я0-9]".toRegex())
        ) {
            Text(
                text = course.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 15.dp)
            )
        }

        Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)) {
            AnimatedVisibility(visible = course.is_paid) {
                Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = course.display_price,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = textPaddingsModifier
                    )
                }
            }

            Button(
                onClick = {
                    navigateToCourseDetails(course)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.details), textAlign = TextAlign.End,
                    modifier = textPaddingsModifier
                        .weight(1f)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                    contentDescription = stringResource(R.string.details)
                )
            }
        }
    }
}

@Composable
fun CourseCover(
    course: Course,
    coverHeight: Dp = 120.dp,
    detailed: Boolean = false,
    cornersRadius: Dp = 12.dp,
    modifier: Modifier = Modifier,
    toggleFavorite: (Course) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .height(coverHeight)
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = course.cover,
            contentDescription = course.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(coverHeight)
                .padding(0.dp)
                .clip(RoundedCornerShape(cornersRadius))
        )

        val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        AnimatedVisibility(
            visible = detailed && backPressDispatcher != null,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            FloatingActionButton(
                onClick = backPressDispatcher!!::onBackPressed,
                containerColor = MaterialTheme.colorScheme.surfaceTint,
                contentColor = MaterialTheme.colorScheme.surface,
                shape = CircleShape,
                modifier = Modifier
                    .padding(start = 15.dp, top = 35.dp, end = 5.dp, bottom = 5.dp)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(0.dp)
                )
            }
        }

        val bookmarkButtonModifier = when (detailed) {
            true -> Modifier
                .padding(start = 5.dp, top = 35.dp, end = 15.dp, bottom = 5.dp)
                .size(44.dp)

            else -> Modifier
                .padding(15.dp)
                .size(34.dp)
        }.align(Alignment.TopEnd)

        var bookmarked by remember { mutableStateOf(course.is_favorite) }
        FloatingActionButton(
            onClick = {
                toggleFavorite(course)
                bookmarked = !bookmarked
            },
            containerColor = when (detailed) {
                true -> MaterialTheme.colorScheme.surfaceTint
                else -> MaterialTheme.colorScheme.surfaceDim
            },
            contentColor = when (detailed) {
                true -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.onSurface
            },
            shape = CircleShape,
            modifier = bookmarkButtonModifier.testTag("favoriteButton")
        ) {
            Icon(
                imageVector = when (bookmarked) {
                    true -> Icons.Outlined.Bookmark
                    else -> Icons.Outlined.BookmarkBorder
                },
                contentDescription = "Add to favorites",
                tint = when (detailed) {
                    true -> MaterialTheme.colorScheme.surface
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier
                    .size(
                        when (detailed) {
                            true -> 24.dp
                            else -> 16.dp
                        }
                    )
                    .padding(0.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceDim,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .height(intrinsicSize = IntrinsicSize.Max)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 5.dp)
                    )

                    Text(
                        text = ((course.readiness * 100f).roundToInt() / 10f).toString(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            AnimatedVisibility(visible = course.create_date != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceDim,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    val formatter = remember { SimpleDateFormat("d MMMM yyyy") }
                    Text(
                        text = formatter.format(course.create_date!!),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseCardPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseCard(
            course = Course(
                id = 123L,
                title = "Testing title",
                summary = "Testing summary",
                description = "Testing description",
                cover = "",
                canonical_url = "https://stepik.org/course/1",
                continue_url = "/course/1/continue",
                authors = listOf(123L),
                readiness = 0.89f,
                is_paid = true,
                display_price = "15000 ₽",
                create_date = Date(),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CourseCardFavoriteAndFreePreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseCard(
            course = Course(
                id = 123L,
                title = "Testing title",
                summary = "Testing summary",
                description = "Testing description",
                cover = "",
                canonical_url = "https://stepik.org/course/1",
                continue_url = "/course/1/continue",
                authors = emptyList(),
                is_favorite = true,
                readiness = 0.75f,
                is_paid = false,
                display_price = "-",
                create_date = Date(),
            ),
        )
    }
}
