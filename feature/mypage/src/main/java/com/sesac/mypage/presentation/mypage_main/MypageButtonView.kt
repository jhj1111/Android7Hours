package com.sesac.mypage.presentation.mypage_main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.borderMicro
import com.sesac.common.ui.theme.elevationSmall
import com.sesac.common.ui.theme.iconSizeMedium
import com.sesac.common.ui.theme.paddingSmall

@Composable
fun MypageButtonView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector?,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke = BorderStroke(borderMicro, MaterialTheme.colorScheme.primary),
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = colors,
        border = border,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevationSmall)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    it,
                    contentDescription = null,
                    modifier = Modifier.size(iconSizeMedium)
                )
            }
            Spacer(modifier = Modifier.width(paddingSmall))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = paddingSmall)
            )
        }
    }
}

@Preview
@Composable
fun MypageButtonViewPreview(){
    Android7HoursTheme {
        MypageButtonView(
            onClick = {},
            text = "버튼",
            icon = Icons.Default.CatchingPokemon
        )
    }
}