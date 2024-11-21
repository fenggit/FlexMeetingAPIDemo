package io.agora.educore.test.view

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/15
 * description :
 */
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ListFragmentDialog : DialogFragment() {

    companion object {
        const val ITEMS_KEY = "items"
    }

    private var listener: OnItemSelectedListener? = null
    fun setOnItemSelectedListener(listener: OnItemSelectedListener): ListFragmentDialog {
        this.listener = listener
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arguments?.getStringArray(ITEMS_KEY) ?: emptyArray()
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Select an Item")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        builder.setAdapter(adapter) { dialog, which ->
            // Handle item click and pass the selected item to the listener
            val selectedItem = items[which]
            listener?.onItemSelected(which, selectedItem)
            dismiss()
        }
        return builder.create()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
