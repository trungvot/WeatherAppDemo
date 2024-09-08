package com.trungvothanh.weatherapplication.fragment

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.trungvothanh.weatherapplication.databinding.AddLocationDialogFragmentBinding
import com.trungvothanh.weatherapplication.helpers.Constants

class AddLocationDialogFragment : DialogFragment() {

    private var _binding: AddLocationDialogFragmentBinding? = null
    private val binding get() = _binding
    private var listener: AddDialogListener? = null

    interface AddDialogListener {
        fun onAddButtonClick(inputText: String)
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = AddLocationDialogFragmentBinding.inflate(LayoutInflater.from(context))

        // Initialize suggestions
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, Constants.CITY_NAME_LIST)
        _binding?.locationNameTxt?.setAdapter(adapter)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding?.root)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding?.addButton?.setOnClickListener {
            val inputText = binding?.locationNameTxt?.text.toString()
            listener?.onAddButtonClick(inputText)
            dialog.dismiss()
        }

        _binding?.cancelButton?.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Attach the listener to the DialogFragment
    fun setListener(listener: AddDialogListener) {
        this.listener = listener
    }
}