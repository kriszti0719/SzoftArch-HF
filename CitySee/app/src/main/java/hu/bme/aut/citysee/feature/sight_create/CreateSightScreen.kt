package hu.bme.aut.citysee.feature.sight_create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.citysee.R
import hu.bme.aut.citysee.ui.common.CitySeeAppBar
import hu.bme.aut.citysee.ui.common.SightEditor
import hu.bme.aut.citysee.util.UiEvent
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun CreateSightScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateSightViewModel = viewModel(factory = CreateSightViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    val hostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.Success -> {
                    onNavigateBack()
                }
                is UiEvent.Failure -> {
                    scope.launch {
                        hostState.showSnackbar(uiEvent.message.asString(context))
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        topBar = {
            CitySeeAppBar(
                title = stringResource(id = R.string.app_bar_title_create_sight),
                onNavigateBack = onNavigateBack,
                actions = { }
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { viewModel.onEvent(CreateSightEvent.SaveSight) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            SightEditor(
                titleValue = state.sight.name,
                titleOnValueChange = { viewModel.onEvent(CreateSightEvent.ChangeName(it)) },
                descriptionValue = state.sight.description,
                descriptionOnValueChange = { viewModel.onEvent(CreateSightEvent.ChangeDescription(it)) },
                selectedType = state.sight.type,
                onTypeSelected = { viewModel.onEvent(CreateSightEvent.SelectType(it)) },
                modifier = Modifier
            )
        }
    }
}