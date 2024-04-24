package com.strainteam.gastoscompartidos.view.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.adapter.ItemUserAdapter
import com.strainteam.gastoscompartidos.databinding.DialogNewEventsBinding
import com.strainteam.gastoscompartidos.databinding.FragmentHomeBinding
import com.strainteam.gastoscompartidos.databinding.SheetSearchUserBinding
import com.strainteam.gastoscompartidos.model.User
import com.strainteam.gastoscompartidos.viewmodel.home.fragments.HomeFragViewModel


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val viewModel: HomeFragViewModel by viewModels()
    private var motivoList = ArrayList<String>()
    private var cuotaList = ArrayList<String>()
    private lateinit var adapter : ItemUserAdapter
    private var mUser : MutableList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observadores
        viewModel.motivoList.observe(viewLifecycleOwner, Observer {
            motivoList = it
        })

        viewModel.cuotaList.observe(viewLifecycleOwner, Observer {
            cuotaList = it
        })

        viewModel.messageToast.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
        })

        viewModel.hideProgress.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.progress.visibility = View.GONE
                binding.tvNuevoEvento.isEnabled = true
            }
        })

        viewModel.userList.observe(viewLifecycleOwner, Observer {
            mUser.clear()
            mUser.addAll(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.showDialogEvent.observe(viewLifecycleOwner, Observer {
            val dialog = MaterialAlertDialogBuilder(requireContext())
            val bindingDialog = DialogNewEventsBinding.inflate(layoutInflater)
            dialog.setView(bindingDialog.root)
            dialog.setCancelable(false)
            bindingDialog.spTipoEvento.adapter = ArrayAdapter(requireContext(), R.layout.spinner_list,R.id.tvTextSpinner, motivoList)
            bindingDialog.spTipoCuota.adapter = ArrayAdapter(requireContext(), R.layout.spinner_list,R.id.tvTextSpinner, cuotaList)
            val builder = dialog.create()
            builder.show()
            bindingDialog.btnCancel.setOnClickListener {
                builder.dismiss()
            }
            bindingDialog.tvAgregarParticipantes.setOnClickListener {
                val builder = BottomSheetDialog(requireContext())
                val bindingSheet = SheetSearchUserBinding.inflate(layoutInflater)
                builder.setContentView(bindingSheet.root)

                bindingSheet.rvUser.layoutManager = LinearLayoutManager(requireContext())
                adapter = ItemUserAdapter(requireContext(), mUser)
                bindingSheet.rvUser.adapter = adapter
                viewModel.getUserExceptMe()

                builder.show()
                bindingSheet.tvCerrar.setOnClickListener {
                    builder.dismiss()
                }

            }
        })

        //evento a click
        binding.tvNuevoEvento.setOnClickListener {
            binding.tvNuevoEvento.isEnabled = false
            binding.progress.visibility = View.VISIBLE
            viewModel.getTipoCuota()
        }
    }

}