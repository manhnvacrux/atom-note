package tech.acruxsolutions.atomnote.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomenote.databinding.ActivityReceiveNoteBinding
import tech.acruxsolutions.atomnote.helpers.NotesHelper
import tech.acruxsolutions.atomnote.miscellaneous.getFilteredSpans
import tech.acruxsolutions.atomnote.miscellaneous.setOnNextAction
import tech.acruxsolutions.atomnote.xml.Note
import java.io.File
import java.util.*
import kotlin.collections.HashSet

class ReceiveNote : AppCompatActivity() {

    private lateinit var binding: ActivityReceiveNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFinishOnTouchOutside(true)

        binding.EnterTitle.setOnNextAction {
            binding.EnterBody.requestFocus()
        }

        if (intent.action == Intent.ACTION_SEND) {
            handleSharedNote()
        }
    }

    private fun handleSharedNote() {
        val title = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        val body = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)

        binding.SaveButton.setOnClickListener {
            val enteredTitle = binding.EnterTitle.text.toString().trim()
            val enteredBody = binding.EnterBody.text

            if (enteredTitle.isNotEmpty() || enteredBody.isNotEmpty()) {
                saveNote(enteredTitle, enteredBody)
                Toast.makeText(this, getString(R.string.saved_to_atomenote), Toast.LENGTH_LONG).show()
            }
            finish()
        }

        binding.EnterBody.setText(body)
        binding.EnterTitle.setText(title)
    }

    private fun saveNote(title: String, body: Editable) {
        val notesHelper = NotesHelper(this)

        val timestamp = Date().time.toString()
        val file = File(notesHelper.getNotePath(), "$timestamp.xml")

        val note = Note(
            title,
            file.path,
            HashSet(),
            timestamp,
            body.toString().trimEnd(),
            body.getFilteredSpans()
        )
        note.writeToFile()

        finish()
    }
}