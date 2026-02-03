package com.sesac.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.sesac.common.R
import com.sesac.common.ui.theme.TextSecondary
import com.sesac.common.ui.theme.Typography
import com.sesac.common.ui.theme.paddingMicro

@Composable
fun CommonExpandableText(text: String, minimizedMaxLines: Int = 2) {
    var isExpanded by remember { mutableStateOf(false) }
    val canExpand = text.lines().size > minimizedMaxLines || text.length > 100

    Column(modifier = Modifier.Companion.clickable(enabled = canExpand) {
        isExpanded = !isExpanded
    }) {
        Text(
            text = text,
            style = Typography.bodyMedium,
            maxLines = if (isExpanded || !canExpand) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Companion.Ellipsis
        )
        if (canExpand && !isExpanded) {
            Text(
                text = stringResource(R.string.common_view_more),
                style = Typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.Companion.padding(top = paddingMicro)
            )
        }
    }
}