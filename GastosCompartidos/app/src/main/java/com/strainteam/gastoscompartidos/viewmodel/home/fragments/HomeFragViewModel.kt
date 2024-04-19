package com.strainteam.gastoscompartidos.viewmodel.home.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class HomeFragViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
}