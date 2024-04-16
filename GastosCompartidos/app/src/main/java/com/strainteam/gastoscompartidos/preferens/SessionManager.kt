package com.strainteam.gastoscompartidos.preferens

import android.content.Context
import android.content.SharedPreferences
import com.strainteam.gastoscompartidos.R

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object{
        const val EMAILUSER = "email"
        const val UIDUSER = "uid"
    }

    fun saveEmail(email: String){
        val editor = prefs.edit()
        editor.putString(EMAILUSER, email)
        editor.apply()
    }
    fun fetchEmail(): String? {
        return prefs.getString(EMAILUSER, null)
    }

    fun saveUid(uid: String){
        val editor = prefs.edit()
        editor.putString(UIDUSER, uid)
        editor.apply()
    }
    fun fetchUid(): String? {
        return prefs.getString(UIDUSER, null)
    }

    fun deleteSession(){
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}