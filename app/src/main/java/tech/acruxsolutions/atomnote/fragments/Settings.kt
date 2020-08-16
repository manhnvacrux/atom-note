package tech.acruxsolutions.atomnote.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomnote.helpers.ExportHelper
import tech.acruxsolutions.atomnote.miscellaneous.Constants

class Settings : PreferenceFragmentCompat() {

    private lateinit var mContext: Context
    private lateinit var exportHelper: ExportHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exportHelper =
            ExportHelper(mContext, this)

        val themePreference: ListPreference? = findPreference(mContext.getString(R.string.themeKey))

        val maxItemsPref: EditTextPreference? = findPreference(mContext.getString(R.string.maxItemsToDisplayInListKey))
        val maxLinesPref: EditTextPreference? = findPreference(mContext.getString(R.string.maxLinesToDisplayInNoteKey))

        val exportNotesPref: Preference? = findPreference(mContext.getString(R.string.exportNotesToAFileKey))
        val importNotesPref: Preference? = findPreference(mContext.getString(R.string.importNotesFromAFileKey))

        val ratePref: Preference? = findPreference(mContext.getString(R.string.rateKey))

        exportNotesPref?.setOnPreferenceClickListener {
            exportHelper.exportBackup()
            return@setOnPreferenceClickListener true
        }

        importNotesPref?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/xml"
            }

            startActivityForResult(intent, Constants.RequestCodeImportFile)
            return@setOnPreferenceClickListener true
        }




        maxItemsPref?.setOnPreferenceChangeListener { preference, newValue ->
            return@setOnPreferenceChangeListener newValue.toString().isNotEmpty()
        }

        maxLinesPref?.setOnPreferenceChangeListener { preference, newValue ->
            return@setOnPreferenceChangeListener newValue.toString().isNotEmpty()
        }

        themePreference?.setOnPreferenceChangeListener { preference, newValue ->
            when (newValue) {
                getString(R.string.darkKey) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                getString(R.string.lightKey) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                getString(R.string.followSystemKey) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.RequestCodeExportFile && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                exportHelper.writeFileToStream(uri)
            }
        }
        if (requestCode == Constants.RequestCodeImportFile && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                val inputStream = mContext.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    exportHelper.importBackup(inputStream)
                }
            }
        }
    }


    private fun openLink(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    companion object {
        private const val PrettyTime = "https://github.com/ocpsoft/prettytime"
        private const val MaterialComponents = "https://github.com/material-components/material-components-android"
    }
}