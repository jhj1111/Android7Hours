package com.sesac.common.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.domain.model.Breed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonDropDownMenuBox(
    isDropdownExpanded: MutableState<Boolean>,
    selectedItem: String,
    content: @Composable (() -> Unit),
) {
    ExposedDropdownMenuBox(expanded = isDropdownExpanded.value, onExpandedChange = { isDropdownExpanded.value = it }) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded.value) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.mypage_select_breed)) }
        )
        ExposedDropdownMenu(expanded = isDropdownExpanded.value, onDismissRequest = { isDropdownExpanded.value = false }) {
            content()
        }
    }
}

@Preview
@Composable
fun CommonDropDownMenuBoxPreview(){
    Android7HoursTheme {
        val isDropdownExpanded = remember { mutableStateOf(false) }
        val breeds = listOf(Breed(breedName = "치와와"), Breed(breedName = "불독"))

        CommonDropDownMenuBox(
            isDropdownExpanded = isDropdownExpanded,
            selectedItem = "치와와",
        ) {
            breeds.forEach { breed ->
                DropdownMenuItem(text = { Text(breed.breedName) }, onClick = {})
            }
        }
    }
}

@Preview
@Composable
fun CommonDropDownMenuBoxExpandedPreview(){
    Android7HoursTheme {
        val isDropdownExpanded = remember { mutableStateOf(true) }
        val breeds = listOf(Breed(breedName = "치와와"), Breed(breedName = "불독"))

        CommonDropDownMenuBox(
            isDropdownExpanded = isDropdownExpanded,
            selectedItem = "",
        ) {
            breeds.forEach { breed ->
                DropdownMenuItem(text = { Text(breed.breedName) }, onClick = {})
            }
        }
    }
}