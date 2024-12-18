package com.github.antonkonyshev.stepic.presentation.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.stepic.R
import com.github.antonkonyshev.stepic.domain.model.Author
import com.github.antonkonyshev.stepic.presentation.courselist.LoadingSpinner
import com.github.antonkonyshev.stepic.ui.theme.StepicTheme

@Composable
fun AccountScreen(viewModel: AccountViewModel = viewModel(), modifier: Modifier = Modifier) {

    if (viewModel.loading.collectAsStateWithLifecycle().value) {
        LoadingSpinner(
            viewModel.loading.collectAsStateWithLifecycle().value,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        if (
            viewModel.authRepository.authenticated.collectAsStateWithLifecycle().value &&
            viewModel.authRepository.account.collectAsStateWithLifecycle().value != null
        ) {
            AccountDetails(viewModel.authRepository.account.collectAsStateWithLifecycle().value!!)
        } else {
            AuthenticationForm(onSubmit = viewModel::authenticate)
        }
    }

}

@Composable
fun AccountDetails(account: Author) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = account.full_name,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
fun AuthenticationForm(onSubmit: (String, String) -> Unit = { _, _ -> }) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.authorization),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )

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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(R.string.email)) },
                placeholder = { Text(text = "user@example.com") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() },
                    onDone = {
                        if (password.isBlank()) passwordFocusRequester.requestFocus() else submit()
                    }
                ),
                modifier = Modifier.padding(bottom = 15.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(R.string.password)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(
                                imageVector = Icons.Outlined.VisibilityOff,
                                contentDescription = stringResource(R.string.hide_password)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = stringResource(R.string.show_password)
                            )
                        }
                    }
                },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submit() },
                    onPrevious = { emailFocusRequester.requestFocus() },
                ),
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .padding(bottom = 25.dp)
            )

            Button(
                onClick = { submit() },
            ) {
                Text(
                    text = stringResource(R.string.sign_in),
                    modifier = Modifier.padding(10.dp)
                )
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

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun AccountScreenPreview() {
    StepicTheme(darkTheme = true, dynamicColor = false) {
        AccountDetails(
            Author(
                id = 123L,
                full_name = "Testing Test",
                avatar = "https://stepik.org/users/992043824/7ab46b3f62579386c21166eece2c443869012cd1/avatar.svg"
            )
        )
    }
}