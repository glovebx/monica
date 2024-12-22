package com.teobaranga.monica.contacts.data

import com.skydoves.sandwich.ApiResponse
import com.teobaranga.monica.data.sync.SyncStatus
import me.tatarka.inject.annotations.Inject

@Inject
class ContactNewSynchronizer(
    private val contactApi: ContactApi,
    private val contactDao: ContactDao,
    private val contactRequestMapper: ContactRequestMapper,
    private val contactEntityMapper: ContactEntityMapper,
) {

    suspend fun sync() {
        // TODO check for network before syncing

        val newContacts = contactDao.getBySyncStatus(SyncStatus.NEW)

        for (entry in newContacts) {
            val request = contactRequestMapper(entry)
            when (val response = contactApi.createContact(request)) {
                is ApiResponse.Success -> {
                    val entity = contactEntityMapper(response.data.data)
                    contactDao.sync(entry.contactId, entity)
                }

                else -> {
                    println("ERROR $response")
                }
            }
        }
    }
}
