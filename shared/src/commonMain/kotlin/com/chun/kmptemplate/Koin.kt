package com.chun.kmptemplate

import co.touchlab.kermit.Kermit
import com.chun.kmptemplate.domain.domainModule
import com.chun.kmptemplate.viewmodel.BaseViewModel
import com.chun.kmptemplate.viewmodel.viewModelModule
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kotlinx.datetime.Clock
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.definition.Definition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.module
import kotlin.reflect.KClass

lateinit var koin: Koin
lateinit var log: Kermit
fun initKoin(appModule: Module): KoinApplication {
    val koinApplication = startKoin {
        modules(
            appModule,
            platformModule,
            coreModule,
            domainModule,
            viewModelModule,
        )
    }

    val koin = koinApplication.koin
    log = koin.get<Kermit> { parametersOf(null) }
    val appInfo =
        koin.get<AppInfo>() // AppInfo is a Kotlin interface with separate Android and iOS implementations
    log.v { "App Id ${appInfo.appId}" }

    return koinApplication
}

val json = kotlinx.serialization.json.Json {
    isLenient = true
    ignoreUnknownKeys = true
}

private val coreModule = module {
    single<Clock> {
        Clock.System
    }
    single<HttpClient> {
        log.v("Init core") { "init http client" }
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json = json)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        log.v("Network") { message }
                    }
                }
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                val timeout = 30000L
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
        }
    }
}

// Used in kermit to inject tag
internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module

expect inline fun <reified T : BaseViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: Definition<T>
): Pair<Module, InstanceFactory<T>>

@Suppress("UNCHECKED_CAST")
fun <T> Koin.getDependency(clazz: KClass<*>): T {
    return get(clazz, null) { parametersOf(clazz.simpleName) } as T
}
