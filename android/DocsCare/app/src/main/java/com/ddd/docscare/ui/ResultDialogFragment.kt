package com.ddd.docscare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ddd.docscare.R


class ResultDialogFragment: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO bundle setting
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_result_dialog, container)

        initLayout(v)

        return v

    }

    private fun initLayout(v: View) {
        val btnShare = v.findViewById<Button>(R.id.btnShare)
        val btnGoHome = v.findViewById<Button>(R.id.btnShare)

        btnShare.setOnClickListener { dismissDialog() }
        btnGoHome.setOnClickListener { dismissDialog() }
    }

    private fun dismissDialog() {
        dismiss()
    }

    companion object {
        private const val CATEGORY = "CATEGORY"
        private const val TEMP_IMAGE_PATH = "TEMP_IMAGE_PATH"

        fun getInstance(category: String = "C7",
                        tempImagePath: String = ""): ResultDialogFragment {
            val bundle = Bundle()
            bundle.putString(CATEGORY, category)
            bundle.putString(TEMP_IMAGE_PATH, tempImagePath)
            val df = ResultDialogFragment()
            df.arguments = bundle
            return df
        }
    }
}