package io.github.droidkaigi.confsched2022.feature.contributors

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import io.github.droidkaigi.confsched2022.designsystem.theme.KaigiScaffold
import io.github.droidkaigi.confsched2022.designsystem.theme.KaigiTheme
import io.github.droidkaigi.confsched2022.designsystem.theme.KaigiTopAppBar
import io.github.droidkaigi.confsched2022.feature.contributors.ContributorsUiModel.ContributorsState.Loaded

@Composable
fun ContributorsScreenRoot(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {}
) {
    val viewModel = hiltViewModel<ContributorsViewModel>()
    val uiModel by viewModel.uiModel
    Contributors(uiModel, onNavigationIconClick, modifier)
}

@Composable
fun Contributors(
    uiModel: ContributorsUiModel,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KaigiScaffold(
        topBar = {
            KaigiTopAppBar(onNavigationIconClick = onNavigationIconClick)
        }
    ) {
        if (uiModel.contributorsState !is Loaded) {
            CircularProgressIndicator()
            return@KaigiScaffold
        }
        val contributors = uiModel.contributorsState.contributors
        val context = LocalContext.current

        LazyColumn(
            modifier = modifier.fillMaxWidth()
        ) {
            items(items = contributors, key = { it.id }) { contributor ->
                val userNameAcronym = contributor.username[0]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(top = 16.dp)
                        .clickable {
                            contributor.profileUrl?.let { url ->
                                try {
                                    Intent(Intent.ACTION_VIEW).also {
                                        it.setPackage("com.github.android")
                                        it.data = Uri.parse(url)
                                        context.startActivity(it)
                                    }
                                } catch (e: ActivityNotFoundException) {
                                    navigateToCustomTab(
                                        url = url,
                                        context = context,
                                    )
                                }
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    AsyncImage(
                        model = contributor.iconUrl,
                        contentDescription = contributor.username,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clip(CircleShape)
                    )
                    Text(
                        text = contributor.username,
                        style = TextStyle(
                            fontWeight = FontWeight(500),
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

private fun navigateToCustomTab(url: String, context: Context) {
    val uri = Uri.parse(url)
    CustomTabsIntent.Builder().also { builder ->
        builder.setShowTitle(true)
        builder.build().also {
            it.launchUrl(context, uri)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContributorsPreview() {
    KaigiTheme {
        ContributorsScreenRoot()
    }
}
