package com.teobaranga.monica.contacts.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teobaranga.kotlin.inject.viewmodel.runtime.compose.injectedViewModel
import com.teobaranga.monica.contacts.detail.bio.ui.ContactInfoBioSection
import com.teobaranga.monica.contacts.detail.ui.ContactInfoContactSection
import com.teobaranga.monica.contacts.detail.ui.ContactInfoRelationshipsSection
import com.teobaranga.monica.contacts.edit.ContactEditRoute
import com.teobaranga.monica.ui.PreviewPixel4
import com.teobaranga.monica.ui.Zero
import com.teobaranga.monica.ui.avatar.UserAvatar
import com.teobaranga.monica.ui.navigation.LocalNavigator
import com.teobaranga.monica.ui.theme.MonicaTheme
import kotlinx.coroutines.launch

@Composable
internal fun ContactDetail(
    viewModel: ContactDetailViewModel = injectedViewModel<ContactDetailViewModel>(),
) {
    val navigator = LocalNavigator.current
    val contactDetail by viewModel.contact.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ContactDetailEffect.Deleted -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    Crossfade(
        targetState = contactDetail,
        label = "ContactDetailScreen",
    ) { contactDetail ->
        when (contactDetail) {
            null -> {
                // TODO Loading
            }

            else -> {
                ContactDetailScreen(
                    contactDetail = contactDetail,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactDetailScreen(contactDetail: ContactDetail) {
    val navigator = LocalNavigator.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = contactDetail.fullName,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigator::popBackStack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navigator.navigate(ContactEditRoute(contactDetail.id))
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Edit contact",
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.Zero,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val pagerState = rememberPagerState(
                pageCount = { contactDetail.infoSections.size },
            )
            val coroutineScope = rememberCoroutineScope()
            PrimaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                contactDetail.infoSections.forEachIndexed { index, infoSection ->
                    ContactDetailTab(
                        text = infoSection.title,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selected = pagerState.currentPage == index,
                    )
                }
            }
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
                verticalAlignment = Alignment.Top,
            ) { page ->
                val contactInfoSection = contactDetail.infoSections[page]
                contactInfoSection.Content(
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
private fun ContactDetailTab(text: String, onClick: () -> Unit, selected: Boolean, modifier: Modifier = Modifier) {
    Tab(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
    )
}

@Composable
@PreviewPixel4
private fun PreviewContactDetailScreen() {
    MonicaTheme {
        ContactDetailScreen(
            contactDetail = ContactDetail(
                id = 1,
                fullName = "John Doe (Johnny)",
                infoSections = listOf(
                    ContactInfoBioSection(
                        name = "John Doe",
                        nickname = "(Johnny)",
                        userAvatar = UserAvatar(
                            contactId = -1,
                            initials = "JD",
                            color = "#709512",
                            avatarUrl = null,
                        ),
                        birthday = null,
                        gender = null,
                    ),
                    ContactInfoContactSection,
                    ContactInfoRelationshipsSection,
                ),
            ),
        )
    }
}
