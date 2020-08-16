package tech.acruxsolutions.atomnote.fragments

import android.os.Bundle
import android.view.View
import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomnote.helpers.NotesHelper
import tech.acruxsolutions.atomnote.miscellaneous.Constants
import tech.acruxsolutions.atomnote.miscellaneous.Operation
import tech.acruxsolutions.atomnote.xml.BaseNote

class DisplayLabel : AtomNoteFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val label = arguments?.get(Constants.argLabelKey).toString()
        model.label = label
        model.fetchLabelledNotes()
    }


    override fun getObservable() = model.labelledNotes

    override fun getFragmentID() = R.id.DisplayLabelFragment

    override fun getBackground() = R.drawable.colored_label

    override fun getSupportedOperations(notesHelper: NotesHelper, baseNote: BaseNote): ArrayList<Operation> {
        return ArrayList()
    }
}