package com.strainteam.gastoscompartidos.view.optionEvents

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ActivityOptionEventsBinding
import com.strainteam.gastoscompartidos.viewmodel.optionEvents.OptionEventsViewModel

class OptionEvents : AppCompatActivity() {
    private lateinit var binding: ActivityOptionEventsBinding
private val viewModel : OptionEventsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name").toString()
        val id = intent.getStringExtra("id").toString()
        binding.tvEvent.text = name

        //lanzadores de eventos y Clicks
        viewModel.getIsOrganizadorEvent(id)

        binding.back.setOnClickListener {
            finish()
        }

        //Observadores
        viewModel.isOrganizador.observe(this, Observer {
            if(it){
                binding.tvEditEvent.visibility = View.VISIBLE
                binding.tvBanco.visibility = View.VISIBLE
                binding.tvCuotaFija.visibility = View.VISIBLE
                binding.tvDeleteEvent.setText("Eliminar evento")
            }else{
                binding.tvDeleteEvent.setText("Salirme del evento (No participaré)")
                binding.tvEditEvent.visibility = View.GONE
                binding.tvBanco.visibility = View.GONE
                binding.tvCuotaFija.visibility = View.GONE
            }
        })

        viewModel.messageToast.observe(this, Observer {
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        })

        viewModel.showView.observe(this, Observer {
            if(it){
                binding.progressBar.visibility = View.GONE
                binding.vista.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.vista.visibility = View.GONE
            }
        })

    }
}