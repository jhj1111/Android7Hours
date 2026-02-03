package com.sesac.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sesac.common.ui.theme.Android7HoursTheme
import com.sesac.common.ui.theme.paddingLarge
import com.sesac.common.ui.theme.paddingMedium
import com.sesac.common.ui.theme.paddingMicro
import com.sesac.common.ui.theme.paddingSmall
import com.sesac.common.ui.theme.primaryContainer
import com.sesac.domain.model.Post

@Composable
fun <T> CommonListContainer(
    modifier: Modifier = Modifier,
    title: String,
    itemList: List<T>,
    placeHolder: Any = Icons.Default.Star,
    emptyStateMessage: String,
    emptyStateSubMessage: String,
    itemContent: @Composable (T) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = paddingLarge)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Surface(
                shape = CircleShape,
                color = primaryContainer
            ) {
                Text(
                    text = "${itemList.size}개",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = paddingMedium, vertical = paddingMicro)
                )
            }
        }

        if (itemList.isEmpty()) {
            CommonEmptyState(
                placeHolder = placeHolder,
                message = emptyStateMessage,
                subMessage = emptyStateSubMessage
            )
        } else {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(paddingSmall)
            ) {
                itemList.forEach{ item ->
                    itemContent(item)
                }
//                items(itemList) { item ->
//                    itemContent(item)
//                }
            }
        }

    }
}

@Preview
@Composable
fun ListContainerViewPreview() {
    Android7HoursTheme {
        CommonListContainer(
            title = "리스트트",
//            itemList = listOf(Post.EMPTY),
            itemList = emptyList<Post>(),
            emptyStateMessage = "빈화면",
            emptyStateSubMessage = "빈빈화면",
            itemContent = {},
        )
    }
}