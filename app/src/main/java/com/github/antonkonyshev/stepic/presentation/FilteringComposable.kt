package com.github.antonkonyshev.stepic.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme

@Composable
fun CourseListFiltering(searchQuery: String, onSearch: (String) -> Unit = {}) {
    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Max)
            .padding(start = 15.dp, top = 35.dp, end = 15.dp, bottom = 0.dp)
    ) {
        var query by remember { mutableStateOf(searchQuery) }
        TextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = {
                Text(
                    text = "${stringResource(R.string.search_courses)}...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alpha(0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onSearch(query) }
                )
            },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = "Clear",
                        modifier = Modifier.size(24.dp).clickable {
                            query = ""
                            onSearch(query)
                        }
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch(query) },
                onDone = { onSearch(query) },
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        query = searchQuery
                    }
                }
        )

        Button(
            onClick = {

            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            contentPadding = PaddingValues(0.dp),
            shape = CircleShape,
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxHeight()
        ) {
            Icon(
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = stringResource(R.string.filters),
                modifier = Modifier
                    .size(24.dp)
                    .padding(0.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseListFilteringPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseListFiltering("")
    }
}

@Preview(showBackground = true)
@Composable
fun CourseListFilteringWithSearchQueryPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseListFiltering("Testing")
    }
}
