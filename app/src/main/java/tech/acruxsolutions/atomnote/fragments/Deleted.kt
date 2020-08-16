package tech.acruxsolutions.atomnote.fragments

import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomnote.helpers.NotesHelper
import tech.acruxsolutions.atomnote.miscellaneous.Operation
import tech.acruxsolutions.atomnote.xml.BaseNote

class Deleted : AtomNoteFragment() {

    override fun getObservable() = model.deletedNotes

    override fun getFragmentID() = R.id.DeletedFragment

    override fun getBackground() = R.drawable.colored_delete

    override fun getSupportedOperations(notesHelper: NotesHelper, baseNote: BaseNote): ArrayList<Operation> {
        val operations = ArrayList<Operation>()
        operations.add(
            Operation(
                R.string.restore,
                R.drawable.restore
            ) { model.restoreFile(baseNote.filePath) })
        operations.add(
            Operation(
                R.string.delete_forever,
                R.drawable.delete
            ) { confirmDeletion(baseNote) })
        return operations
    }
}