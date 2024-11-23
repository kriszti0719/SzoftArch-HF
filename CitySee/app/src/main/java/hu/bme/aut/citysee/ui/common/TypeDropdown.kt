package hu.bme.aut.citysee.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.bme.aut.citysee.ui.model.TypeUi

@ExperimentalMaterial3Api
@Composable
fun TypeDropDown(
    types: List<TypeUi>,
    selectedType: TypeUi,
    onTypeSelected: (TypeUi) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val angle: Float by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    val shape = RoundedCornerShape(5.dp)
    Surface(
        modifier = modifier
            .clip(shape = shape)
            .width(TextFieldDefaults.MinWidth)
            .background(MaterialTheme.colorScheme.background)
            .height(TextFieldDefaults.MinHeight)
            .clickable(enabled = enabled) { expanded = true },
        shape = shape
    ) {
        Row(
            modifier = modifier
                .width(TextFieldDefaults.MinWidth)
                .height(TextFieldDefaults.MinHeight)
                .clip(shape = shape),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                tint = selectedType.color,
                modifier = Modifier
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                modifier = Modifier
                    .weight(weight = 8f),
                text = stringResource(id = selectedType.title),
                style = MaterialTheme.typography.labelMedium
            )
            if (enabled) { // only allow the user to switch the type if in edit mode
                IconButton(
                    modifier = Modifier
                        .rotate(degrees = angle)
                        .weight(weight = 1.5f),
                    onClick = { expanded = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
            DropdownMenu(
                modifier = modifier
                    .width(TextFieldDefaults.MinWidth),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = type.title),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        onClick = {
                            expanded = false
                            onTypeSelected(type)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = null,
                                tint = type.color,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                }
            }
        }
    }


}

@ExperimentalMaterial3Api
@Composable
//@Preview
fun TypeDropdown_Preview() {
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TypeDropDown(
            types = types,
            selectedType = selectedType,
            onTypeSelected = {
                selectedType = it
            }
        )

    }
}
