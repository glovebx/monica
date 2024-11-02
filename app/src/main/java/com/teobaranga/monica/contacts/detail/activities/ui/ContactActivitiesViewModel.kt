package com.teobaranga.monica.contacts.detail.activities.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teobaranga.monica.activities.data.ContactActivitiesRepository
import com.teobaranga.monica.activities.data.ContactActivitiesSynchronizer
import com.teobaranga.monica.core.dispatcher.Dispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = ContactActivitiesViewModel.Factory::class)
internal class ContactActivitiesViewModel @AssistedInject constructor(
    contactActivitiesRepository: ContactActivitiesRepository,
    dispatcher: Dispatcher,
    @Assisted
    private val contactId: Int,
    private val contactActivitiesSynchronizerFactory: ContactActivitiesSynchronizer.Factory,
) : ViewModel() {

    val contactActivities = contactActivitiesRepository.getActivities(contactId)
        .mapLatest { contactActivities ->
            val activities = contactActivities
                .map { contactActivityWithParticipants ->
                    contactActivityWithParticipants.toExternalModel(contactId)
                }
            if (activities.isEmpty()) {
                ContactActivitiesUiState.Empty
            } else {
                ContactActivitiesUiState.Loaded(activities)
            }
        }
        .onStart {
            viewModelScope.launch(dispatcher.io) {
                contactActivitiesSynchronizerFactory.create(contactId)
                    .sync()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = ContactActivitiesUiState.Loading,
        )

    @AssistedFactory
    interface Factory {
        fun create(contactId: Int): ContactActivitiesViewModel
    }
}
