package com.teobaranga.monica.contacts.edit

import ContactsNavGraph
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.teobaranga.monica.contacts.edit.ui.ContactEditTopAppBar
import com.teobaranga.monica.inject.runtime.injectedViewModel

data class ContactEditNavArgs(
    val contactId: Int? = null,
    val contactName: String? = null,
)

@Destination<ContactsNavGraph>(
    navArgs = ContactEditNavArgs::class,
)
@Composable
internal fun ContactEdit(
    navigator: DestinationsNavigator,
    viewModel: ContactEditViewModel = injectedViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ContactEditScreen(
        uiState = uiState,
        topBar = {
            ContactEditTopAppBar(
                onBack = navigator::popBackStack,
                onDelete = {
                    viewModel.onDelete()
                    navigator.popBackStack()
                },
            )
        },
        onSave = {
            viewModel.onSave()
            navigator.popBackStack()
        },
    )
}
