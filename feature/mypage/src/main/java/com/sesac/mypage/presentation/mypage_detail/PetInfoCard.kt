package com.sesac.mypage.presentation.mypage_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.sesac.common.R
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.Gray200
import com.sesac.common.ui.theme.Gray500
import com.sesac.common.ui.theme.Primary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.White
import com.sesac.common.ui.theme.cardImageSizeMicro
import com.sesac.common.ui.theme.elevationMedium
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.utils.sampleIconImageUrl
import com.sesac.domain.model.Pet

@Composable
fun PetInfoCard(
    pet: Pet,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteClicked()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevationMedium)
    ) {
        Box {
            Column(modifier = Modifier.Companion.padding(paddingLarge)) {
                Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                    AsyncImage(
                        model = pet.image ?: R.drawable.placeholder,
                        contentDescription = pet.name,
                        modifier = Modifier.Companion
                            .size(cardImageSizeMicro)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Companion.Crop
                    )
                    Spacer(modifier = Modifier.Companion.width(paddingMedium))
                    Column() {
                        Text(
                            text = pet.name,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Companion.Bold
                        )
                        Text(text = pet.breed ?: "", style = Typography.bodyMedium, color = Gray500)
                    }
                }
                Spacer(modifier = Modifier.Companion.height(paddingMedium))
                Row(horizontalArrangement = Arrangement.spacedBy(paddingMedium)) {
                    PetDetailChip(
                        modifier = Modifier.Companion.weight(1f),
                        icon = Icons.Default.Cake,
                        label = stringResource(R.string.common_birthday),
                        value = pet.birthday ?: ""
                    )
                    PetDetailChip(
                        modifier = Modifier.Companion.weight(1f),
                        icon = Icons.Default.FavoriteBorder,
                        label = stringResource(R.string.common_gender),
                        value = pet.gender ?: ""
                    )
                }
            }

            Box(modifier = Modifier.Companion.align(Alignment.Companion.TopEnd)) {
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.common_action_edit)) },
                        onClick = {
                            onEditClicked()
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.common_action_delete)) },
                        onClick = {
                            showDeleteDialog = true
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PetDetailChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    ) {
    Row(
        modifier = modifier
            .background(Gray200, MaterialTheme.shapes.medium)
            .padding(paddingMedium),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = Primary)
        Spacer(modifier = Modifier.Companion.width(paddingSmall))
        Column {
            Text(text = label, style = Typography.bodySmall, color = Gray500)
            Text(
                text = value,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Companion.SemiBold
            )
        }
    }
}

@Preview
@Composable
fun PetInfoCardPreview(){
    Android7HoursTheme {
        PetInfoCard(
            pet = Pet.EMPTY.copy(owner = "dd", name = "뽀삐", gender = "M", image = sampleIconImageUrl, birthday = "2020-01-01",),
            onEditClicked = {},
            onDeleteClicked = {},
        )
    }
}