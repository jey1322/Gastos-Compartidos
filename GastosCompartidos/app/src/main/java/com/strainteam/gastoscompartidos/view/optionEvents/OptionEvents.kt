package com.strainteam.gastoscompartidos.view.optionEvents

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ActivityOptionEventsBinding
import com.strainteam.gastoscompartidos.databinding.DialogBancosBinding
import com.strainteam.gastoscompartidos.databinding.DialogCuotaFijaBinding
import com.strainteam.gastoscompartidos.databinding.DialogPedidoBinding
import com.strainteam.gastoscompartidos.databinding.DialogUpdateEventoBinding
import com.strainteam.gastoscompartidos.utils.CalendarComplFragment
import com.strainteam.gastoscompartidos.view.login.MainActivity
import com.strainteam.gastoscompartidos.viewmodel.optionEvents.OptionEventsViewModel

class OptionEvents : AppCompatActivity() {
    private lateinit var binding: ActivityOptionEventsBinding
    private val viewModel : OptionEventsViewModel by viewModels()
    private var bancosList = ArrayList<String>()
    private var motivosEventList = ArrayList<String>()
    private var tipoCuotaList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name").toString()
        val id = intent.getStringExtra("id").toString()
        binding.tvEvent.text = name

        //lanzadores de eventos y Clicks
        viewModel.getDetalleEvento(id)

        binding.back.setOnClickListener {
            finish()
        }

        binding.tvCuotaPagada.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
            dialog.setTitle("Marcar como pagada?")
            dialog.setMessage("Realiza esta acción siempre y cuando ya hayas depositado tu cuota al organizador")
            dialog.setPositiveButton("Marcar pagada"){ _, _ ->
                viewModel.marcarComoPagada(id)
                binding.vista.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            dialog.setNegativeButton("Cancelar"){ _, _ -> }
            dialog.setCancelable(false)
            dialog.show()
        }

        binding.tvPedido.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
            val bindingDialog = DialogPedidoBinding.inflate(layoutInflater)
            dialog.setView(bindingDialog.root)
            bindingDialog.etPedido.setText(viewModel.pedido.value)
            dialog.setPositiveButton("Guardar pedido"){ _, _ ->
                viewModel.updateMiPedido(id,bindingDialog.etPedido.text.toString())
                binding.vista.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            dialog.setNegativeButton("Cancelar"){ _, _ -> }
            dialog.setCancelable(false)
            dialog.show()
        }

        binding.tvSalirmeEvento.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
            dialog.setTitle("Salirme del evento")
            dialog.setMessage("Estas seguro de salirte de este evento?")
            dialog.setPositiveButton("Salirme"){ _, _ ->
                viewModel.salirEvento(id)
                binding.vista.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            dialog.setNegativeButton("Cancelar"){ _, _ -> }
            dialog.setCancelable(false)
            dialog.show()
        }

        binding.tvDeleteEvent.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
            dialog.setTitle("Eliminar evento")
            dialog.setMessage("Estas seguro de eliminar este evento?")
            dialog.setPositiveButton("Eliminar"){ _, _ ->
                viewModel.deleteEvent(id)
                binding.vista.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            dialog.setNegativeButton("Cancelar"){ _, _ -> }
            dialog.setCancelable(false)
            dialog.show()
        }

        binding.tvCuotaFija.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
            dialog.setCancelable(false)
            val bindingDialog = DialogCuotaFijaBinding.inflate(layoutInflater)
            dialog.setView(bindingDialog.root)
            dialog.setPositiveButton("Guardar"){ _, _ ->
                viewModel.updateValueTotalaDepositar(id,bindingDialog.etCuotaVal.text.toString().toInt())
                binding.vista.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            dialog.setNegativeButton("Cancelar"){ _, _ -> }
            dialog.show()
        }

        binding.tvBanco.setOnClickListener {
            viewModel.getBancos()
            binding.vista.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }

        binding.tvEditEvent.setOnClickListener {
            viewModel.getMotivosEventos()
            binding.vista.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }

        //Observadores
        viewModel.isOrganizador.observe(this, Observer {
            if(it){
                binding.tvEditEvent.visibility = View.VISIBLE
                binding.tvBanco.visibility = View.VISIBLE
                binding.tvCuotaFija.visibility = View.VISIBLE
                binding.tvDeleteEvent.visibility = View.VISIBLE
                binding.tvSalirmeEvento.visibility = View.GONE
                viewModel.tipoCuota.observe(this, Observer {
                    if(it=="Cuota Fija"){
                        binding.tvCuotaFija.visibility = View.VISIBLE
                    }else{
                        binding.tvCuotaFija.visibility = View.GONE
                    }
                })
            }else{
                binding.tvSalirmeEvento.visibility = View.VISIBLE
                binding.tvDeleteEvent.visibility = View.GONE
                binding.tvEditEvent.visibility = View.GONE
                binding.tvBanco.visibility = View.GONE
                binding.tvCuotaFija.visibility = View.GONE
            }
        })

        viewModel.tipoEvento.observe(this, Observer {
            if(it == "Cooperación"){
                binding.tvPedido.visibility = View.GONE
            }else{
                binding.tvPedido.visibility = View.VISIBLE
            }
        })

        viewModel.isPagado.observe(this, Observer {
            if(it){
                binding.tvCuotaPagada.text = "Cuota pagada"
                binding.tvCuotaPagada.background = getDrawable(R.drawable.cuota_pagada)
                binding.tvCuotaPagada.isEnabled = false
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

        viewModel.backScreen.observe(this, Observer {
            if(it){
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        })

        viewModel.bancosList.observe(this, Observer {
            bancosList = it
        })

        viewModel.showDialogBanco.observe(this, Observer {
            if(it){
                val dialog = MaterialAlertDialogBuilder(this)
                dialog.setCancelable(false)
                val bindingDialog = DialogBancosBinding.inflate(layoutInflater)
                dialog.setView(bindingDialog.root)
                bindingDialog.spBancos.adapter = ArrayAdapter(this, R.layout.spinner_list,R.id.tvTextSpinner, bancosList)
                dialog.setPositiveButton("Guardar"){ _, _ ->
                    viewModel.updateCuentaYBancoOrganizador(id,bindingDialog.spBancos.selectedItem.toString(),bindingDialog.etCuenta.text.toString())
                    binding.vista.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                dialog.setNegativeButton("Cancelar"){ _, _ -> }
                dialog.show()
            }
        })

        viewModel.motivoEventList.observe(this, Observer {
            motivosEventList = it
        })

        viewModel.tipoCuotaList.observe(this, Observer {
            tipoCuotaList = it
        })

        viewModel.showDialogEdit.observe(this, Observer {
            if(it){
                val dialog = MaterialAlertDialogBuilder(this)
                dialog.setCancelable(false)
                val bindingDialog = DialogUpdateEventoBinding.inflate(layoutInflater)
                dialog.setView(bindingDialog.root)
                bindingDialog.spTipoEvento.adapter = ArrayAdapter(this, R.layout.spinner_list, R.id.tvTextSpinner, motivosEventList)
                bindingDialog.spTipoCuota.adapter = ArrayAdapter(this, R.layout.spinner_list, R.id.tvTextSpinner, tipoCuotaList)
                bindingDialog.etEvento.setText(viewModel.evento.value)
                bindingDialog.etFechaEvento.setText(viewModel.fecha.value)
                bindingDialog.spTipoEvento.setSelection(motivosEventList.indexOf(viewModel.tipoEvento.value))
                bindingDialog.spTipoCuota.setSelection(tipoCuotaList.indexOf(viewModel.tipoCuota.value))
                bindingDialog.etFechaEvento.setOnClickListener {
                    CalendarComplFragment { bindingDialog.etFechaEvento.setText(it) }.show(this.supportFragmentManager, "datePicker")
                }
                dialog.setPositiveButton("Actualizar"){ _, _ ->
                    viewModel.updateEvento(id,bindingDialog.etEvento.text.toString(),bindingDialog.etFechaEvento.text.toString(),bindingDialog.spTipoEvento.selectedItem.toString(),bindingDialog.spTipoCuota.selectedItem.toString())
                    binding.vista.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                dialog.setNegativeButton("Cancelar"){ _, _ -> }
                dialog.show()
            }
        })

    }
}