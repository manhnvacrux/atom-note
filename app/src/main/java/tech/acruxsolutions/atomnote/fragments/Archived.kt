package tech.acruxsolutions.atomnote.fragments

import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomnote.helpers.NotesHelper
import tech.acruxsolutions.atomnote.miscellaneous.Operation
import tech.acruxsolutions.atomnote.xml.BaseNote

class Archived : AtomNoteFragment() {

    override fun getObservable() = model.archivedNotes

    override fun getFragmentID() = R.id.ArchivedFragment

    override fun getBackground() = R.drawable.colored_archive

    override fun getSupportedOperations(notesHelper: NotesHelper, baseNote: BaseNote): ArrayList<Operation> {
        val operations = ArrayList<Operation>()
        operations.add(
            Operation(
                R.string.share,
                R.drawable.share
            ) { notesHelper.shareNote(baseNote) })
        operations.add(
            Operation(
                R.string.labels,
                R.drawable.label
            ) { labelBaseNote(baseNote) })
        operations.add(
            Operation(
                R.string.unarchive,
                R.drawable.unarchive
            ) { model.restoreFile(baseNote.filePath) })
        return operations
    }
}