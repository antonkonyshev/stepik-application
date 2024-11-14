package com.github.antonkonyshev.stepic.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.domain.Course
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CourseCard(course: Course) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier.padding(12.dp)
    ) {
        GlideImage(
            model = course.cover, contentDescription = course.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(120.dp)
                .padding(0.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        val textPaddingsModifier = remember {
            Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        }

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
                onClick = {},
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

@Preview(showBackground = true)
@Composable
fun CourseCardPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseCard(
            Course(
                id = 123L,
                title = "Testing title",
                summary = "Testing summary",
                cover = "",
                is_paid = true,
                display_price = "15000 ₽",
                create_date = "2024-11-11T15:00"
            )
        )
    }
}