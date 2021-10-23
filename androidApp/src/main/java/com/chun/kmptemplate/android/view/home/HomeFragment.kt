package com.chun.kmptemplate.android.view.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.chun.kmptemplate.android.R
import com.chun.kmptemplate.android.databinding.HomeBinding
import com.chun.kmptemplate.android.util.viewBinding
import com.chun.kmptemplate.platform

class HomeFragment : Fragment(R.layout.home) {
    private val binding by viewBinding(HomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.text.text = platform
    }
}
