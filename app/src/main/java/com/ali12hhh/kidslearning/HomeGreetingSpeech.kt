package com.ali12hhh.kidslearning

import android.app.Application

class KidsLearningApp : Application() {
    override fun onCreate() {
        super.onCreate()
        HomeGreetingSpeech.initialize(this)
    }
}
