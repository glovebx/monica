package com.teobaranga.monica.network.config

import com.diamondedge.logging.logging
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.experimental.ExperimentalNativeApi

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, multibinding = true)
class LoggingHttpClientConfigurator : HttpClientConfigurator {

    @OptIn(ExperimentalNativeApi::class)
    override fun HttpClientConfig<*>.configure() {
        if (Platform.isDebugBinary) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        log.d { message }
                    }
                }
                level = LogLevel.BODY
            }
        }
    }

    companion object {
        private val log = logging("HttpLogging")
    }
}
