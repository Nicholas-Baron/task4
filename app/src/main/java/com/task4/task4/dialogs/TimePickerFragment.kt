package com.task4.task4.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.core.os.bundleOf
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME = "time"

class TimePickerFragment : DialogFragment() {

    interface Callbacks {

        fun onTimeSelected(time: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_TIME) as Date
        val calendar = Calendar.getInstance().apply { time = date }

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val resultDate = GregorianCalendar(
                initialYear, initialMonth, initialDayOfMonth, hourOfDay, minute
            ).time
            (targetFragment as Callbacks?)?.onTimeSelected(resultDate)
        }

        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val is24Hour = DateFormat.is24HourFormat(requireContext())

        return TimePickerDialog(requireContext(), timeListener, hourOfDay, minute, is24Hour)
    }

    companion object {

        fun newInstance(time: Date) = TimePickerFragment().apply {
            arguments = bundleOf(ARG_TIME to time)
        }
    }
}