package com.strainteam.gastoscompartidos.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ItemEventosBinding
import com.strainteam.gastoscompartidos.model.Eventos
import com.strainteam.gastoscompartidos.view.optionEvents.OptionEvents

class ItemEventos(private val context: Context, private val mList: MutableList<Eventos>): RecyclerView.Adapter<ItemEventos.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemEventosBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(evento: Eventos){
            binding.tvEvento.text = evento.evento
            binding.tvFecha.text = evento.fecha
            binding.tvOrganizador.text = "Organizador: "+evento.organizadorName
            binding.tvBanco.text = "Banco: "+evento.bancoOrganizador
            binding.tvTipoEventoVal.text = evento.tipoEvento
            binding.tvTipoCuotaVal.text = evento.tipoCuota
            binding.tvCuotaTotal.text = "Cuota a depositar: "+evento.participantes[0].totalDepositar.toString()

            binding.tvCopy.setOnClickListener {
                if(evento.bancoOrganizador.isEmpty()){
                    Toast.makeText(context, "No hay datos para copiar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else{
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Banco del Organizador", evento.bancoOrganizador)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "cuenta copiada", Toast.LENGTH_SHORT).show()
                }
            }

            binding.tvOptions.setOnClickListener {
                val intent = Intent(context, OptionEvents::class.java)
                intent.putExtra("id", evento.id)
                intent.putExtra("name", evento.evento)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemEventosBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    fun updateData(newUsers: List<Eventos>) {
        mList.clear()
        mList.addAll(newUsers)
        notifyDataSetChanged()
    }
}