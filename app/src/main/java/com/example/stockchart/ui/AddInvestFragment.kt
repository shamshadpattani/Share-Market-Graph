package com.example.stockchart.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.stockchart.R
import com.example.stockchart.databinding.ActivityAddInvestBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_add_invest.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class AddInvestFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: InvestViewModel
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
        viewModel = ViewModelProvider(this).get(InvestViewModel::class.java)
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
        init()
       subscribe()
        setAdjustScreen();
    }

    private fun setAdjustScreen() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private fun subscribe() {
        viewModel.dataError.observe(viewLifecycleOwner, {error->
            if(error){
                unit.setTextColor(ContextCompat.getColor(context!!,R.color.design_default_color_error))
                viewModel.seletedDate.postValue(null)
            }
        })

        viewModel.dismissDialog.observe(viewLifecycleOwner,{dismiss ->
            if(dismiss) {
                dismiss()
            }
        })
    }

    private fun init() {
        inputDate_text.apply {
            inputType = InputType.TYPE_NULL
            keyListener = null
            setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showDatePicker() }
            setOnClickListener { showDatePicker() }
        }

        problem_save_btn.setOnClickListener {
            when {
                viewModel.seletedDate.value==null -> {
                    binding.inputDate.error="fill it"
                }
                viewModel.amount.value==null -> {
                    binding.inputamount.error="must be fill it"
                }
                else -> {
                    viewModel.saveUserInvest()
                }
            }

        }
        problem_cancel_btn.setOnClickListener {
           viewModel.discardChanges()
        }
    }

    private fun showDatePicker() {
        val date = viewModel.seletedDate.value
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.date_text)

        if (date != null) {
         val dateLong= date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            builder.setSelection(dateLong)
            val constraintsBuilder = CalendarConstraints.Builder()
            constraintsBuilder.setOpenAt(dateLong)
            builder.setCalendarConstraints(constraintsBuilder.build())
        }
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            val newDate = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()

            viewModel.seletedDate.postValue(newDate)
        }
        picker.show(childFragmentManager, "CHECKUP_DATE")
    }
}