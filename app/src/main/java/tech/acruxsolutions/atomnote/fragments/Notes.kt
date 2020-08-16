package tech.acruxsolutions.atomnote.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomnote.activities.MainActivity
import tech.acruxsolutions.atomnote.activities.MakeList
import tech.acruxsolutions.atomnote.activities.TakeNote
import tech.acruxsolutions.atomnote.helpers.MenuHelper
import tech.acruxsolutions.atomnote.helpers.NotesHelper
import tech.acruxsolutions.atomnote.miscellaneous.Constants
import tech.acruxsolutions.atomnote.miscellaneous.Operation
import tech.acruxsolutions.atomnote.xml.BaseNote

class Notes : AtomNoteFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (mContext as MainActivity).binding.TakeNoteFAB.setOnClickListener {
            displayNoteTypes()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.Search) {
            findNavController().navigate(R.id.NotesFragmentToSearchFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
    }


    private fun displayNoteTypes() {
        val menuHelper = MenuHelper(mContext)

        menuHelper.addItem(R.string.make_list, R.drawable.checkbox) {
            val intent = Intent(mContext, MakeList::class.java)
            intent.putExtra(Constants.PreviousFragment, R.id.NotesFragment)
            startActivityForResult(intent, Constants.RequestCode)
        }
        menuHelper.addItem(R.string.take_note, R.drawable.edit) {
            val intent = Intent(mContext, TakeNote::class.java)
            intent.putExtra(Constants.PreviousFragment, R.id.NotesFragment)
            startActivityForResult(intent, Constants.RequestCode)
        }

        menuHelper.show()
    }


    override fun getObservable() = model.notes

    override fun getFragmentID() = R.id.NotesFragment

    override fun getBackground() = R.drawable.notebook

    override fun getSupportedOperations(notesHelper: NotesHelper, baseNote: BaseNote) : ArrayList<Operation> {
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