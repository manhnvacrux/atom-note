package tech.acruxsolutions.atomnote.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import tech.acruxsolutions.atomenote.R
import tech.acruxsolutions.atomnote.activities.MainActivity
import tech.acruxsolutions.atomnote.adapters.BaseNoteAdapter
import tech.acruxsolutions.atomenote.databinding.FragmentNotesBinding
import tech.acruxsolutions.atomnote.helpers.ExportHelper
import tech.acruxsolutions.atomnote.helpers.MenuHelper
import tech.acruxsolutions.atomnote.helpers.NotesHelper
import tech.acruxsolutions.atomnote.helpers.SettingsHelper
import tech.acruxsolutions.atomnote.miscellaneous.Constants
import tech.acruxsolutions.atomnote.miscellaneous.ItemDecoration
import tech.acruxsolutions.atomnote.miscellaneous.Operation
import tech.acruxsolutions.atomnote.viewmodels.BaseNoteModel
import tech.acruxsolutions.atomnote.xml.BaseNote
import java.io.File

abstract class AtomNoteFragment : Fragment() {

    internal lateinit var mContext: Context
    private lateinit var adapter: BaseNoteAdapter
    private lateinit var exportHelper: ExportHelper

    internal var binding: FragmentNotesBinding? = null

    internal val model: BaseNoteModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        exportHelper =
            ExportHelper(mContext, this)

        adapter = BaseNoteAdapter(mContext)
        adapter.onNoteClicked = this::onBaseNoteClicked
        adapter.onNoteLongClicked = this::onBaseNoteLongClicked
        binding?.RecyclerView?.adapter = adapter
        binding?.RecyclerView?.setHasFixedSize(true)

        setupPadding()
        setupImageView()
        setupLayoutManager()
        setupItemDecoration()

        populateRecyclerView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotesBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val filePath = data?.getStringExtra(Constants.FilePath)

        if (requestCode == Constants.RequestCodeExportFile && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                exportHelper.writeFileToStream(uri)
            }
        } else when (resultCode) {
            Constants.ResultCodeCreatedFile -> {
                if (filePath != null) {
                    val file = File(filePath)
                    if (file.exists()) {
                        val baseNote = BaseNote.readFromFile(file)
                        if (baseNote.isEmpty()) {
                            model.moveFileToDeleted(filePath)
                            val rootView = (mContext as MainActivity).binding.CoordinatorLayout
                            Snackbar.make(rootView, baseNote.getEmptyMessage(), Snackbar.LENGTH_SHORT).show()
                        } else binding?.RecyclerView?.layoutManager?.scrollToPosition(0)
                    }
                }
            }
        }
    }


    private fun onBaseNoteClicked(position: Int) {
        val baseNote = adapter.currentList[position]
        val intent = Intent(mContext, baseNote.getAssociatedActivity())
        intent.putExtra(Constants.FilePath, baseNote.filePath)
        intent.putExtra(Constants.PreviousFragment, getFragmentID())
        startActivityForResult(intent, Constants.RequestCode)
    }

    private fun onBaseNoteLongClicked(position: Int) {
        val baseNote = adapter.currentList[position]
        val menuHelper = MenuHelper(mContext)
        val notesHelper =
            NotesHelper(mContext)

        val operations = getSupportedOperations(notesHelper, baseNote)
        if (operations.isNotEmpty()) {
            operations.forEach { operation -> menuHelper.addItem(operation) }
            menuHelper.show()
        }
    }


    private fun setupPadding() {
        val padding = mContext.resources.getDimensionPixelSize(R.dimen.recyclerViewPadding)
        val settingsHelper =
            SettingsHelper(mContext)
        val cardPreference = settingsHelper.getNoteTypePreferences()

        if (cardPreference == mContext.getString(R.string.elevatedKey)) {
            binding?.RecyclerView?.setPaddingRelative(padding, 0, padding, 0)
        }
    }

    private fun setupImageView() {
        binding?.ImageView?.setImageResource(getBackground())
    }

    private fun setupLayoutManager() {
        val settingsHelper =
            SettingsHelper(mContext)
        val viewPreference = settingsHelper.getViewPreference()

        binding?.RecyclerView?.layoutManager = if (viewPreference == mContext.getString(R.string.gridKey)) {
            StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        } else LinearLayoutManager(mContext)
    }

    private fun setupItemDecoration() {
        val settingsHelper =
            SettingsHelper(mContext)
        val cardMargin = mContext.resources.getDimensionPixelSize(R.dimen.cardMargin)
        val viewPreference = settingsHelper.getViewPreference()
        val cardPreference = settingsHelper.getNoteTypePreferences()

        if (cardPreference == mContext.getString(R.string.elevatedKey)) {
            if (viewPreference == mContext.getString(R.string.gridKey)) {
                binding?.RecyclerView?.addItemDecoration(
                    ItemDecoration(
                        cardMargin,
                        2
                    )
                )
            } else binding?.RecyclerView?.addItemDecoration(
                ItemDecoration(
                    cardMargin,
                    1
                )
            )
        }
    }


    internal fun labelBaseNote(baseNote: BaseNote) {
        val notesHelper =
            NotesHelper(mContext)
        notesHelper.labelNote(baseNote.labels) { labels ->
            model.editNoteLabel(baseNote, labels)
        }
    }

    internal fun confirmDeletion(baseNote: BaseNote) {
        val alertDialogBuilder = MaterialAlertDialogBuilder(mContext)
        alertDialogBuilder.setMessage(R.string.delete_note_forever)
        alertDialogBuilder.setPositiveButton(R.string.delete) { dialog, which ->
            model.deleteFileForever(baseNote.filePath)
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel, null)
        alertDialogBuilder.show()
    }

    internal fun showExportDialog(baseNote: BaseNote) {
        val file = File(baseNote.filePath)
        val menuHelper = MenuHelper(mContext)

        menuHelper.addItem(R.string.pdf, R.drawable.pdf) { exportHelper.exportFileToPDF(file) }
        menuHelper.addItem(R.string.html, R.drawable.html) { exportHelper.exportFileToHTML(file) }
        menuHelper.addItem(R.string.plain_text, R.drawable.plain_text) { exportHelper.exportFileToPlainText(file) }

        menuHelper.show()
    }


    private fun populateRecyclerView() {
        getObservable().observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(ArrayList(list))
            confirmVisibility(list)
        })
    }

    private fun confirmVisibility(notes: List<BaseNote>) {
        if (notes.isNotEmpty()) {
            binding?.RecyclerView?.visibility = View.VISIBLE
        } else binding?.RecyclerView?.visibility = View.GONE
    }


    abstract fun getFragmentID(): Int

    abstract fun getBackground(): Int

    abstract fun getObservable(): MutableLiveData<ArrayList<BaseNote>>

    abstract fun getSupportedOperations(notesHelper: NotesHelper, baseNote: BaseNote): ArrayList<Operation>
}