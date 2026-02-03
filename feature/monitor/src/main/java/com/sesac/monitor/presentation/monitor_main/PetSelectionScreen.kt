package com.sesac.monitor.presentation.monitor_main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.cardIconSize
import com.sesac.common.ui.theme.elevationSmall
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.domain.model.Pet

@Composable
fun PetSelectionScreen(pets: List<Pet>, onPetSelect: (Pet) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.monitor_pet_select_pet_target),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(paddingLarge))
        if (pets.isEmpty()) {
            Text(stringResource(R.string.monitor_pet_list_empty))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(paddingMedium)) {
                items(pets) { pet ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPetSelect(pet) },
                        elevation = CardDefaults.cardElevation(defaultElevation = elevationSmall)
                    ) {
                        Row(
                            modifier = Modifier.padding(paddingLarge),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Pets,
                                contentDescription = null,
                                modifier = Modifier.size(cardIconSize)
                            )
                            Spacer(modifier = Modifier.width(paddingMedium))
                            Text(pet.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetSelectionScreenPreview() {
    val samplePets = listOf(
        Pet.EMPTY.copy(id = 1, name = "코코", breed = "푸들", linkedUser = "1"),
        Pet.EMPTY.copy(id = 2, name = "보리", breed = "말티즈", linkedUser = "1"),
        Pet.EMPTY.copy(id = 3, name = "레오", breed = "치와와", linkedUser = "1"),
    )
    Android7HoursTheme {
        PetSelectionScreen(pets = samplePets, onPetSelect = {})
    }
}
