package com.strainteam.gastoscompartidos.view.optionEvents

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ActivityOptionEventsBinding

class OptionEvents : AppCompatActivity() {
    private lateinit var binding: ActivityOptionEventsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name").toString()
        binding.tvEvent.text = "Evento: $name"

    }
}