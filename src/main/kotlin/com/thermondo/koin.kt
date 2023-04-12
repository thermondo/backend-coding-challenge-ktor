package com.thermondo

import com.thermondo.service.AuthService
import com.thermondo.service.AuthServiceImpl
import com.thermondo.service.DatabaseFactory
import com.thermondo.service.DatabaseFactoryImpl
import com.thermondo.service.NoteService
import com.thermondo.service.NoteServiceImpl
import org.koin.dsl.module

val serviceKoinModule = module {
    single<NoteService> { NoteServiceImpl(get()) }
    single<AuthService> { AuthServiceImpl(get()) }
}

val databaseKoinModule = module {
    single<DatabaseFactory> { DatabaseFactoryImpl() }
}
