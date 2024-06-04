package com.strainteam.gastoscompartidos.viewmodel.participantes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.adapter.ItemParticipantes
import com.strainteam.gastoscompartidos.model.Eventos
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class ParticipantesViewModel(application: Application, isOrginizador: Boolean, tipoCuota: String, idEvento: String): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private lateinit var auth: FirebaseAuth
    private lateinit var dbEventoRef : DatabaseReference
    private lateinit var database: FirebaseDatabase
    val participantesAdapter = ItemParticipantes(context, mutableListOf(), isOrginizador, tipoCuota, idEvento, this)
    val hideProgress = SingleLiveEvent<Boolean>()
    val showNoParticipantes = SingleLiveEvent<Boolean>()
    val messageToast = SingleLiveEvent<String>()
    val participantesList = MutableLiveData<MutableList<Eventos.Participantes>>()
    val showDialogDelete = SingleLiveEvent<String>()

    init {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbEventoRef = database.reference.child("Eventos")
    }

    fun getParticipantesEvento(idEvento: String){
        dbEventoRef.child(idEvento).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                val participantesList = mutableListOf<Eventos.Participantes>()
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() != auth.currentUser?.uid){
                        participantesList.add(Eventos.Participantes(
                            participante.child("id").value.toString(),
                            participante.child("email").value.toString(),
                            participante.child("name").value.toString(),
                            participante.child("pedido").value.toString(),
                            participante.child("totalDepositar").value.toString().toInt(),
                            participante.child("pagado").value as Boolean
                        ))
                    }
                }
                if(participantesList.isNotEmpty()) {
                    this.participantesList.value = participantesList
                    participantesAdapter.updateData(participantesList)
                    showNoParticipantes.value = false
                }else{
                    showNoParticipantes.value = true
                }
                hideProgress.value = true
            }else{
                hideProgress.value = true
                messageToast.value = "Error al obtener los participantes: ${it.exception?.message}"
            }
        }
    }

    fun showDialogDeleteParticipante(idParticipante: String){
        showDialogDelete.value = idParticipante
    }

    fun deleteParticipante(idEvento: String, idParticipante: String){
        dbEventoRef.child(idEvento).child("Participantes").child(idParticipante).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                messageToast.value = "Participante eliminado"
            }else{
                messageToast.value = "Error al eliminar participante: ${it.exception?.message}"
            }
        }
    }

    fun addtotalDepositarParticipante(idEvento: String, idParticipante: String, totalDepositar: Int){
        dbEventoRef.child(idEvento).child("Participantes").child(idParticipante).child("totalDepositar").setValue(totalDepositar).addOnCompleteListener {
            if(it.isSuccessful){
                messageToast.value = "Total depositar actualizado"
            }else{
                messageToast.value = "Error al actualizar total depositar: ${it.exception?.message}"
            }
        }
    }


}