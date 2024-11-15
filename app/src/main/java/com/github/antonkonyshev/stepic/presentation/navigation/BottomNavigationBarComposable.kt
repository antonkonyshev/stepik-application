package com.github.antonkonyshev.stepic.presentation.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String? = navController.currentBackStackEntryAsState().value?.destination?.route
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
    ) {
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)

        BottomAppBar(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = Color.Transparent,
        ) {
            StepicNavRouting.screens.forEach { screen ->
                NavigationBarItem(
                    selected = currentRoute?.contains(screen.route) ?: false,
                    onClick = {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(id = screen.label)
                        )
                    },
                    alwaysShowLabel = true,
                    label = {
                        Text(stringResource(id = screen.label))
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.surfaceBright,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    StepicTheme(darkTheme = true, dynamicColor = false) {
        BottomNavigationBar(navController, currentRoute = StepicNavRouting.screens[0].route)
    }
}