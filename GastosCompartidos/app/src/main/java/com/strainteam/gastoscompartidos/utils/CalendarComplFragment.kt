package com.strainteam.gastoscompartidos.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class CalendarComplFragment (val listener : (String) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if(month < 10 && dayOfMonth < 10)
            listener("$year/0${month+1}/0$dayOfMonth")
        else if(month < 10)
            listener("$year/0${month+1}/$dayOfMonth")
        else if(dayOfMonth < 10)
            listener("$year/${month+1}/0$dayOfMonth")
        else
            listener("$year/${month+1}/$dayOfMonth")
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val picker = DatePickerDialog(activity as Context, this, year, month, day)
        return picker
    }
}