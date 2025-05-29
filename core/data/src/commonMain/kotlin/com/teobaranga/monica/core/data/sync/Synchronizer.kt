package com.teobaranga.monica.core.data.sync

import kotlinx.coroutines.flow.Flow

interface Synchronizer {

    val syncState: Flow<State>

    suspend fun sync()

    enum class State {
        IDLE,
        REFRESHING,
    }
}
