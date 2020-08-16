package tech.acruxsolutions.atomnote.viewmodels

import android.app.Application
import android.text.Editable
import tech.acruxsolutions.atomnote.miscellaneous.applySpans
import tech.acruxsolutions.atomnote.miscellaneous.getFilteredSpans
import tech.acruxsolutions.atomnote.xml.BaseNote
import tech.acruxsolutions.atomnote.xml.Note

class TakeNoteModel(app: Application) : AtomNoteModel(app) {

    var body = Editable.Factory.getInstance().newEditable(String())

    override fun saveNote() {
        file?.let {
            val note = Note(
                title,
                it.path,
                labels.value ?: HashSet(),
                timestamp.toString(),
                body.toString().trimEnd(),
                body.getFilteredSpans()
            )
            if (it.exists()) {
                val savedNote = BaseNote.readFromFile(it)
                if (savedNote != note) {
                    note.writeToFile()
                }
            } else note.writeToFile()
        }
    }

    override fun setStateFromFile() {
        file?.let { file ->
            if (file.exists()) {
                val baseNote = BaseNote.readFromFile(file) as Note
                title = baseNote.title
                timestamp = baseNote.timestamp.toLong()
                body = baseNote.body.applySpans(baseNote.spans)
                labels.value = baseNote.labels
            }
        }
    }
}