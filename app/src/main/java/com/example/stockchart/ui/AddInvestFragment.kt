package com.example.stockchart.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.stockchart.R
import com.example.stockchart.databinding.ActivityAddInvestBinding
import com.example.stockchart.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddInvestFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding:ActivityAddInvestBinding


    companion object {
        fun newInstances(): AddInvestFragment {
            val f = AddInvestFragment()
            val args = Bundle()
            /* args.putInt(KEY_SUB_QUESTION_ID,sqId)
        args.putInt(KEY_PROBLEM_ID,problemId)
        f.arguments = args*/
            return f
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.activity_add_invest,
            container,
            false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
    /*    init()
        subscribe()*/
    }
}