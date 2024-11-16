package hu.bme.aut.citysee.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.citysee.ui.common.EmailTextField
import hu.bme.aut.citysee.ui.common.PasswordTextField
import hu.bme.aut.citysee.util.UiEvent
import kotlinx.coroutines.launch
import hu.bme.aut.citysee.R.string as StringResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginUserViewModel = viewModel(factory = LoginUserViewModel.Factory)
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = SnackbarHostState()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.Success -> {
                    onSuccess()
                }
                is UiEvent.Failure -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message.asString(context)
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.tertiary)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmailTextField(
                value = state.email, 
                label = stringResource(id = StringResources.textfield_label_email), 
                onValueChange = { viewModel.onEvent(LoginUserEvent.EmailChanged(it)) },
                onDone = {},
                imeAction = ImeAction.Next,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            PasswordTextField(
                value = state.password,
                label = stringResource(id = StringResources.textfield_label_password),
                onValueChange = { viewModel.onEvent(LoginUserEvent.PasswordChanged(it)) },
                onDone = {},
                modifier = Modifier.padding(bottom = 10.dp),
                isVisible = state.passwordVisibility,
                onVisibilityChanged = { viewModel.onEvent(LoginUserEvent.PasswordVisibilityChanged) }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { viewModel.onEvent(LoginUserEvent.SignIn) },
                modifier = Modifier.padding(bottom = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary, // Gomb háttérszíne
                    contentColor = MaterialTheme.colorScheme.tertiary // Gomb szövegének színe
                )
            ) {
                Text(text = stringResource(id = StringResources.button_text_sign_in))
            }
            Button(
                onClick = onRegisterClick,
                modifier = Modifier.padding(bottom = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary, // Gomb háttérszíne
                    contentColor = MaterialTheme.colorScheme.tertiary // Gomb szövegének színe
                )
            ) {
                Text(text = stringResource(id = StringResources.button_text_no_account))
            }
        }
    }
}