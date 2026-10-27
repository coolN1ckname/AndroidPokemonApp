package com.example.androidapp

import android.app.Application
import com.example.androidapp.DI.AppContainer

class PokemonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContainer.initialize(this)
    }
}