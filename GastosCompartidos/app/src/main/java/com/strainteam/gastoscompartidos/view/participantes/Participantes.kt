package com.strainteam.gastoscompartidos.view.participantes

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.strainteam.gastoscompartidos.databinding.ActivityParticipantesBinding
import com.strainteam.gastoscompartidos.viewmodel.participantes.ParticipantesViewModel

class Participantes : AppCompatActivity() {
    private lateinit var binding: ActivityParticipantesBinding
    private val viewModel : ParticipantesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecycler()
        val id = intent.getStringExtra("id").toString()
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
        val name = intent.getStringExtra("name").toString()
        val isOrganizador = intent.getBooleanExtra("isorganizador",false)
        val tipoCuota = intent.getStringExtra("tipocuota").toString()
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