package hu.bme.aut.citysee.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.bme.aut.citysee.R
import hu.bme.aut.citysee.domain.model.Type
import hu.bme.aut.citysee.ui.model.TypeUi
import hu.bme.aut.citysee.ui.model.asTypeUi

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun SightEditor(
    nameValue: String,
    nameOnValueChange: (String) -> Unit,
    addressValue: String,
    addressOnValueChange: (String) -> Unit,
    bonusInfoValue: String,
    bonusInfoOnValueChange: (String) -> Unit,
    descriptionValue: String,
    descriptionOnValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    types: List<TypeUi> = Type.types.map { it.asTypeUi() },
    selectedType: TypeUi,
    onTypeSelected: (TypeUi) -> Unit,
    enabled: Boolean = true,
) {
    val fraction = 0.95f

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        if (enabled) {
            NormalTextField(
                value = nameValue,
                label = stringResource(id = R.string.textfield_label_name),
                onValueChange = nameOnValueChange,
                onDone = { keyboardController?.hide() },
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .padding(top = 5.dp),
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        NormalTextField(
            value = addressValue,
            label = stringResource(id = R.string.textfield_label_address),
            onValueChange = addressOnValueChange,
            onDone = { keyboardController?.hide() },
            imeAction = ImeAction.Next,
            modifier = Modifier
                .fillMaxWidth(fraction)
                .padding(top = 5.dp),
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(5.dp))
        TypeDropDown(
            types = types,
            selectedType = selectedType,
            onTypeSelected = onTypeSelected,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(fraction),
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(5.dp))
        NormalTextField(
            value = descriptionValue,
            label = stringResource(id = R.string.textfield_label_description),
            onValueChange = descriptionOnValueChange,
            onDone = { keyboardController?.hide() },
            imeAction = ImeAction.Done,
            modifier = Modifier
                .weight(5f)
                .fillMaxWidth(fraction)
                .padding(bottom = 5.dp),
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(5.dp))
        NormalTextField(
            value = bonusInfoValue,
            label = stringResource(id = R.string.textfield_label_bonusInfo),
            onValueChange = bonusInfoOnValueChange,
            onDone = { keyboardController?.hide() },
            imeAction = ImeAction.Next,
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth(fraction)
                .padding(top = 5.dp),
            enabled = enabled
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun SightEditor_Preview() {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bonusInfo by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val types = listOf(
        TypeUi.None,
        TypeUi.Museum,
        TypeUi.HistoricalSite,
        TypeUi.Park,
        TypeUi.ArtGallery,
        TypeUi.Theater,
        TypeUi.Zoo,
        TypeUi.Market,
        TypeUi.Bridge,
        TypeUi.Observatory,
        TypeUi.Castle,
        TypeUi.Church,
        TypeUi.Library,
        TypeUi.Restaurant,
        TypeUi.Bar
    )
    var selectedType by remember { mutableStateOf(types[0]) }

    Box(Modifier.fillMaxSize()) {
        SightEditor(
            nameValue = name,
            nameOnValueChange = { name = it },
            addressValue = address,
            addressOnValueChange = { address = it },
            bonusInfoValue = bonusInfo,
            bonusInfoOnValueChange = { bonusInfo = it },
            descriptionValue = description,
            descriptionOnValueChange = { description = it },
            types = types,
            selectedType = selectedType,
            onTypeSelected = { selectedType = it },
        )
    }
}