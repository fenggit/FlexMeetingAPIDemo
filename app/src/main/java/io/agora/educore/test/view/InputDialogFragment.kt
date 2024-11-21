package io.agora.educore.test.view

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/15
 * description :
 */
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.agora.educore.example.R

class InputDialogFragment : DialogFragment() {

    fun interface OnInputListener {
        fun onInputReceived(input: String)
    }

    var onInputListener: OnInputListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        // Inflate the custom layout/view
        val view: View = inflater.inflate(R.layout.fragment_dialog_input, null)
        val editText: EditText = view.findViewById(R.id.editTextInput)

        builder.setView(view)
            .setTitle("Send a message")
            .setPositiveButton("OK") { dialog, id ->
                // Send the input back to the activity
                val input = editText.text.toString()
                onInputListener?.onInputReceived(input)
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

        return builder.create()
    }

    companion object {
        fun newInstance(): InputDialogFragment {
            return InputDialogFragment()
        }
    }
}
