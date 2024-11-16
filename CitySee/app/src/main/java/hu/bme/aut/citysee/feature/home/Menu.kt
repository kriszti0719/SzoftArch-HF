package hu.bme.aut.citysee.feature.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun Menu(
    expanded: Boolean,
    items: Array<MenuItemUiModel>,
    onDismissRequest: () -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier.padding(5.dp),
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(
                text = item.text,
                leadingIcon = item.icon,
                onClick = { onClick(item.screenRoute) },
                modifier = Modifier.clip(RoundedCornerShape(5.dp))
            )
            if (index != items.lastIndex) {
                HorizontalDivider(modifier = Modifier.height(10.dp).padding(vertical = 5.dp))
            }
        }
    }
}