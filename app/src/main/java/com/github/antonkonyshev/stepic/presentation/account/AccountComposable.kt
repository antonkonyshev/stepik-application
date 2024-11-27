package com.github.antonkonyshev.stepic.presentation.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.stepic.presentation.courselist.LoadingSpinner
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme

@Composable
fun AccountScreen(viewModel: AccountViewModel = viewModel(), modifier: Modifier = Modifier) {

    if (viewModel.loading.collectAsStateWithLifecycle().value) {
        AccountDetails()
    } else {
        AuthenticationForm(onSubmit = viewModel::authenticate)
    }

    LoadingSpinner(
        viewModel.loading.collectAsStateWithLifecycle().value,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AccountDetails() {

}

@Composable
fun AuthenticationForm(onSubmit: (String, String) -> Unit = { _, _ -> }) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Authorization", style = MaterialTheme.typography.titleLarge)

            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current
            val emailFocusRequester = FocusRequester()
            val passwordFocusRequester = FocusRequester()
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var showPassword by remember { mutableStateOf(false) }

            fun submit() {
                focusManager.clearFocus()
                keyboardController?.hide()
                onSubmit(email, password)
            }

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                placeholder = { Text(text = "user@example.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() },
                    onDone = {
                        if (password.isBlank()) passwordFocusRequester.requestFocus() else submit()
                    }
                )
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { showPassword != showPassword }) {
                        if (showPassword) {
                            Icon(
                                imageVector = Icons.Outlined.VisibilityOff,
                                contentDescription = "Hide password"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = "Show password"
                            )
                        }
                    }
                },
                visualTransformation = if (showPassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                keyboardActions = KeyboardActions(
                    onDone = { submit() },
                    onPrevious = { emailFocusRequester.requestFocus() },
                ),
                modifier = Modifier.focusRequester(passwordFocusRequester)
            )

            Button(
                onClick = { submit() }
            ) {
                Text(text = "Sign in")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthenticationFormPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        AuthenticationForm()
    }
}