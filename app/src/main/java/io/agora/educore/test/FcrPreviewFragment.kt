package io.agora.educore.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.agora.educore.example.R

/**
 * author : chenbinhang@agora.io
 * date : 2024/10/14
 * description :
 */
class FcrPreviewFragment : Fragment() {
    lateinit var preview: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_preview, container, false)
        preview = rootView.findViewById(R.id.preview_container)
        return rootView
    }
}