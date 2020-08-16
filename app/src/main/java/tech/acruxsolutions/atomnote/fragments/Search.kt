package tech.acruxsolutions.atomnote.fragments

import android.animation.LayoutTransition
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomnote.activities.MainActivity
import tech.acruxsolutions.atomnote.helpers.NotesHelper
import tech.acruxsolutions.atomnote.miscellaneous.Operation
import tech.acruxsolutions.atomnote.xml.BaseNote

class Search : AtomNoteFragment() {

    private var textWatcher: TextWatcher? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.keyword = String()
        model.fetchSearchResults()
        binding?.FrameLayout?.layoutTransition = LayoutTransition()

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(query: Editable?) {
                model.keyword = query.toString().trim()
                model.fetchSearchResults()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        (mContext as MainActivity).binding.EnterSearchKeyword.addTextChangedListener(textWatcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (mContext as MainActivity).binding.EnterSearchKeyword.removeTextChangedListener(textWatcher)
    }


    override fun getObservable() = model.searchResults

    override fun getFragmentID() = R.id.SearchFragment

    override fun getBackground() = R.drawable.colored_search

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
                R.string.export,
                R.drawable.export
            ) { showExportDialog(baseNote) })
        operations.add(
            Operation(
                R.string.delete,
                R.drawable.delete
            ) { model.moveFileToDeleted(baseNote.filePath) })
        operations.add(
            Operation(
                R.string.archive,
                R.drawable.archive
            ) { model.moveFileToArchive(baseNote.filePath) })
        return operations
    }
}