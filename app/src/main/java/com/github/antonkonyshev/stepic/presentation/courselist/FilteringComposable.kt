package com.github.antonkonyshev.stepic.presentation.courselist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme

@Composable
fun CourseListFiltering(
    searchQuery: String,
    ordering: Boolean,
    onSearch: (String) -> Unit = {},
    toggleOrdering: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max)
                .padding(start = 15.dp, top = 35.dp, end = 15.dp, bottom = 0.dp)
        ) {
            SearchTextField(searchQuery, onSearch, modifier = Modifier.weight(1f))

            var showFiltersPanel by remember { mutableStateOf(false) }
            FiltersPanelToggleButton() {
                showFiltersPanel = !showFiltersPanel
            }
        }

        OrderingDropdown(
            ordering = ordering,
            toggleOrdering = toggleOrdering,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun OrderingDropdown(
    ordering: Boolean,
    toggleOrdering: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Button(
        onClick = { toggleOrdering() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "По дате добавляения", style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        AnimatedVisibility(visible = ordering) {
            Icon(
                imageVector = Icons.Outlined.ArrowDownward,
                contentDescription = "Sorting",
                modifier = Modifier
                    .size(16.dp)
                    .padding(0.dp)
            )
        }
        AnimatedVisibility(visible = !ordering) {
            Icon(
                imageVector = Icons.Outlined.ArrowUpward,
                contentDescription = "Sorting",
                modifier = Modifier
                    .size(16.dp)
                    .padding(0.dp)
            )
        }
    }
}

@Composable
fun FiltersPanelToggleButton(onToggle: () -> Unit) {
    Button(
        onClick = { onToggle() },
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

@Composable
fun SearchTextField(
    searchQuery: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
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
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
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
        modifier = modifier
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    query = searchQuery
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun CourseListFilteringPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseListFiltering(searchQuery = "", ordering = false)
    }
}

@Preview(showBackground = true)
@Composable
fun CourseListFilteringWithSearchQueryPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        CourseListFiltering(searchQuery = "Testing", ordering = true)
    }
}
