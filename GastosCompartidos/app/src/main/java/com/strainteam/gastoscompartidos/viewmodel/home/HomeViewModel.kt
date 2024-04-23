package com.strainteam.gastoscompartidos.viewmodel.home

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.view.home.fragments.HomeFragment
import com.strainteam.gastoscompartidos.view.home.fragments.ProfileFragment
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class HomeViewModel: ViewModel() {
    val selectedFragment = SingleLiveEvent<Fragment>()

    fun selectFragment(itemId: Int){
        when(itemId){
            R.id.HomeNb -> {
                selectedFragment.value = HomeFragment()
            } R.id.ProfileNb -> {
                selectedFragment.value = ProfileFragment()
            }
        }
    }
}