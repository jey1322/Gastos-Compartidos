package com.strainteam.gastoscompartidos.view.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ActivityHomeBinding
import com.strainteam.gastoscompartidos.view.home.fragments.HomeFragment
import com.strainteam.gastoscompartidos.viewmodel.home.HomeViewModel

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.selectFragment(R.id.HomeNb)

        viewModel.selectedFragment.observe(this) { fragment ->
            loadFragment(fragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            viewModel.selectFragment(it.itemId)
            true
        }

    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}