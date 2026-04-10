package com.example.projecteandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentDesarrolladora : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Aquí le decimos que muestre tu diseño fragment_desarrolladora.xml
        return inflater.inflate(R.layout.fragment_desarrolladora, container, false)
    }
}