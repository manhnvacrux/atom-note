package tech.acruxsolutions.atomnote

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import tech.acruxsolutions.atomenote.R

class AtomNoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setDarkMode()
    }

    private fun setDarkMode() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = sharedPreferences.getString(getString(R.string.themeKey), getString(
            R.string.followSystemKey
        ))
        when (theme){
            getString(R.string.darkKey) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            getString(R.string.lightKey) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            getString(R.string.followSystemKey) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}