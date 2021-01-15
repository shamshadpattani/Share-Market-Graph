package com.example.stockchart.ui

import android.os.Bundle
import android.text.InputType
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_add_invest.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class AddInvestFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityAddInvestBinding


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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.activity_add_invest,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()
        //subscribe()
    }

    private fun inits() {
        inputDate_text.apply {
            inputType = InputType.TYPE_NULL
            keyListener = null
            setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showDatePicker() }
            setOnClickListener { showDatePicker() }
        }
    }

    private fun showDatePicker() {
        val date = viewModel.seletedDate.value
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.date_text)

        if (date != null) {
         val dateLong = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
         } else {
             TODO("VERSION.SDK_INT < O")
         }
            builder.setSelection(dateLong)
            val constraintsBuilder = CalendarConstraints.Builder()
            constraintsBuilder.setOpenAt(dateLong)
            builder.setCalendarConstraints(constraintsBuilder.build())
        }
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            val newDate =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            viewModel.seletedDate.postValue(newDate)
        }
        picker.show(childFragmentManager, "CHECKUP_DATE")
    }
}