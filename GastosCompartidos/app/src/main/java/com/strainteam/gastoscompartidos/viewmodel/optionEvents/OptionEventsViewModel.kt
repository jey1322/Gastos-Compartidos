package com.strainteam.gastoscompartidos.viewmodel.optionEvents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class OptionEventsViewModel(application: Application): AndroidViewModel(application){
    private val context = getApplication<Application>().applicationContext
    private lateinit var  auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var dbEventoRef : DatabaseReference
    val isOrganizador = SingleLiveEvent<Boolean>()
    val showView = SingleLiveEvent<Boolean>()
    val messageToast = SingleLiveEvent<String>()
    val isPagado = MutableLiveData<Boolean>()
    val pedido = SingleLiveEvent<String>()
    val totalDepositar = SingleLiveEvent<Int>()

    init {
        auth = FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        dbEventoRef= database.reference.child("Eventos")
    }

    fun getMiDetallePartipante(idEvent: String){
        dbEventoRef.child(idEvent).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() == auth.currentUser!!.uid){
                        isPagado.value = participante.child("pagado").value.toString().toBoolean()
                        pedido.value = participante.child("pedido").value.toString()
                        totalDepositar.value = participante.child("totalDepositar").value.toString().toInt()
                    }
                }
                getIsOrganizadorEvent(idEvent)
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
                showView.value = false
            }
        }
    }

    fun getIsOrganizadorEvent(idEvent: String){
        dbEventoRef.child(idEvent).child("OrganizadorId").get().addOnCompleteListener {
            if(it.isSuccessful){
                isOrganizador.value = it.result!!.value.toString() == auth.currentUser!!.uid
                showView.value = true
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
                showView.value = false
            }
        }
    }

    fun marcarComoPagada(idEvent: String){
        dbEventoRef.child(idEvent).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() == auth.currentUser!!.uid){
                        dbEventoRef.child(idEvent).child("Participantes").child(participante.key!!).child("pagado").setValue(true)
                        messageToast.value = "Cuota marcada como pagada"
                        getMiDetallePartipante(idEvent)
                    }
                }
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

    fun updateMiPedido(idEvent: String, pedido: String){
        dbEventoRef.child(idEvent).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() == auth.currentUser!!.uid){
                        dbEventoRef.child(idEvent).child("Participantes").child(participante.key!!).child("pedido").setValue(pedido)
                        messageToast.value = "Pedido actualizado"
                        getMiDetallePartipante(idEvent)
                    }
                }
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

}