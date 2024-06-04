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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ActivityParticipantesBinding
import com.strainteam.gastoscompartidos.databinding.DialogAddCuotaBinding
import com.strainteam.gastoscompartidos.viewmodel.participantes.ParticipantesViewModel

class Participantes : AppCompatActivity() {
    private lateinit var binding: ActivityParticipantesBinding
    private lateinit var viewModel : ParticipantesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isOrganizador = intent.getBooleanExtra("isorganizador",false)
        val tipoCuota = intent.getStringExtra("tipocuota").toString()
        val id = intent.getStringExtra("id").toString()
        val name = intent.getStringExtra("name").toString()

        val viewModelFactory = ParticipantesViewModelFactory(application, isOrganizador, tipoCuota, id)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ParticipantesViewModel::class.java)

        initRecycler()

        binding.tvEvent.setText(name)
        binding.back.setOnClickListener {finish()}
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
        viewModel.showDialogDelete.observe(this) {
            val dialog = MaterialAlertDialogBuilder(this)
                .setBackground(resources.getDrawable(R.drawable.button_cancel))
                .setTitle("Eliminar participante")
                .setMessage("¿Estás seguro de eliminar el participante ${it}?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.deleteParticipante(id, it)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
            dialog.show()
        }
        viewModel.showDialogCuota.observe(this){
            val dialog = MaterialAlertDialogBuilder(this)
            dialog.setBackground(resources.getDrawable(R.drawable.button_cancel))
            dialog.setCancelable(false)
            val bindingDialog = DialogAddCuotaBinding.inflate(layoutInflater)
            dialog.setView(bindingDialog.root)
            dialog.setPositiveButton("Agregar") { _, _ ->
                val cuota = bindingDialog.etCuotaVal.text.toString()
                if(cuota.isNotEmpty()){
                    viewModel.addtotalDepositarParticipante(id, it, cuota.toInt())
                }else{
                    Toast.makeText(this, "Introduce una cuota", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            dialog.show()
        }

    }

    private fun initRecycler(){
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = viewModel.participantesAdapter
    }
}

class ParticipantesViewModelFactory(private val application: Application, private val isOrganizador: Boolean, private val tipoCuota: String, private val idEvento: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParticipantesViewModel::class.java)) {
            return ParticipantesViewModel(application, isOrganizador, tipoCuota, idEvento) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}