package hu.bme.aut.citysee.feature.home

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import hu.bme.aut.citysee.R
import hu.bme.aut.citysee.ui.common.CitySeeAppBar
import kotlinx.coroutines.launch
import hu.bme.aut.citysee.R.string as StringResources

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CitySeeAppBar(
                title = stringResource(id = StringResources.app_bar_title_sights),
                actions = {
                    IconButton(onClick = {
                        onSignOut()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    }
                    IconButton(onClick = {
                        val bundle = Bundle()
                        bundle.putString("demo_key", "idabc")
                        bundle.putString("data_key", "mydata")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Message,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar(message = context.getString(R.string.snackbar_message))
                }
            },
                containerColor = MaterialTheme.colorScheme.primary, // Egyéni háttérszín
                contentColor = MaterialTheme.colorScheme.tertiary, // ikon
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.tertiary),
        ) {
            Text(
                text = stringResource(id = StringResources.welcome_home),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun HomeScreen_Preview() {
    HomeScreen(        onSignOut = {},

        )
}


