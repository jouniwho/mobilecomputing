package com.example.app3

import android.app.Application

class app3Application : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}