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
    val backScreen = SingleLiveEvent<Boolean>()
    val tipoEvento = SingleLiveEvent<String>()
    val tipoCuota = SingleLiveEvent<String>()
    val bancoOrganizador = SingleLiveEvent<String>()
    val cuentaOrganizador = SingleLiveEvent<String>()
    val evento = SingleLiveEvent<String>()
    val fecha = SingleLiveEvent<String>()

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
                        getDetalleEvento(idEvent)
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
                        getDetalleEvento(idEvent)
                    }
                }
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

    fun salirEvento(idEvent: String){
        dbEventoRef.child(idEvent).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() == auth.currentUser!!.uid){
                        dbEventoRef.child(idEvent).child("Participantes").child(participante.key!!).removeValue()
                        messageToast.value = "Has salido del evento"
                        backScreen.value = true
                    }
                }
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

    fun deleteEvent(idEvent: String){
        dbEventoRef.child(idEvent).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                messageToast.value = "Evento eliminado"
                backScreen.value = true
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

    fun getDetalleEvento(idEvent: String){
        dbEventoRef.child(idEvent).get().addOnCompleteListener {
            if(it.isSuccessful){
                tipoEvento.value = it.result!!.child("TipoEvento").value.toString()
                tipoCuota.value = it.result!!.child("TipoCuota").value.toString()
                bancoOrganizador.value = it.result!!.child("BancoOrganizador").value.toString()
                cuentaOrganizador.value = it.result!!.child("CuentaOrganizador").value.toString()
                evento.value = it.result!!.child("Evento").value.toString()
                fecha.value = it.result!!.child("Fecha").value.toString()
                getMiDetallePartipante(idEvent)
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
                showView.value = false
            }
        }
    }

    fun updateValueTotalaDepositar(idEvent: String, totalDepositar: Int){
        dbEventoRef.child(idEvent).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    dbEventoRef.child(idEvent).child("Participantes").child(participante.key!!).child("totalDepositar").setValue(totalDepositar)
                }
                messageToast.value = "Total a depositar actualizado"
                backScreen.value = true
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

}