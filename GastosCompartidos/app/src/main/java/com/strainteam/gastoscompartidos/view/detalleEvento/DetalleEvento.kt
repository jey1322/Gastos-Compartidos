package com.strainteam.gastoscompartidos.view.detalleEvento

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ActivityDetalleEventoBinding

class DetalleEvento : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleEventoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id= intent.getStringExtra("id")
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

    }
}