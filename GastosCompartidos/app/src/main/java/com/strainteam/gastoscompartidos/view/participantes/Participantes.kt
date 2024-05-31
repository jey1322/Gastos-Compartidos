package com.strainteam.gastoscompartidos.view.participantes

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.strainteam.gastoscompartidos.databinding.ActivityParticipantesBinding
import com.strainteam.gastoscompartidos.viewmodel.participantes.ParticipantesViewModel

class Participantes : AppCompatActivity() {
    private var isOrganizador: Boolean = false
    private lateinit var tipoCuota: String
    private lateinit var binding: ActivityParticipantesBinding
    //private val viewModel : ParticipantesViewModel by viewModels() { ParticipantesViewModelFactory(application, isOrganizador, tipoCuota) }
    private lateinit var viewModel : ParticipantesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isOrganizador = intent.getBooleanExtra("isorganizador",false)
        tipoCuota = intent.getStringExtra("tipocuota").toString()
        viewModel = ViewModelProvider(this, ParticipantesViewModelFactory(application, isOrganizador, tipoCuota)).get(ParticipantesViewModel::class.java)
        initRecycler()
        val id = intent.getStringExtra("id").toString()
        val name = intent.getStringExtra("name").toString()

        binding.tvEvent.setText(name)
        viewModel.getParticipantesEvento(id)

        //observadores
        viewModel.hideProgress.observe(this) {
            if (it) { binding.progressBar.visibility = View.GONE }
        }
        viewModel.showNoParticipantes.observe(this) {
            if (it) {
                Toast.makeText(this, "No hay participantes", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.messageToast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.participantesList.observe(this) {
            viewModel.participantesAdapter.notifyDataSetChanged()
        }

    }

    private fun initRecycler(){
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = viewModel.participantesAdapter
    }
}

class ParticipantesViewModelFactory(private val application: Application, private val isOrganizador: Boolean, private val tipoCuota: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParticipantesViewModel::class.java)) {
            return ParticipantesViewModel(application, isOrganizador, tipoCuota) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}