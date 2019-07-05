package com.ddd.docscare.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.ddd.docscare.R
import com.ddd.docscare.base.PP
import com.ddd.docscare.common.CATEGORY_MAP
import com.ddd.docscare.common.DEFAULT_FOLDER_LIST
import com.ddd.docscare.db.dto.FileItemDTO
import com.ddd.docscare.ui.folder.FolderViewModel
import com.ddd.docscare.util.FileUtils
import kotlinx.android.synthetic.main.fragment_result_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ResultDialogFragment: DialogFragment() {

    private var category: String = ""
    private var tempImagePath: String = ""
    private val folderViewModel: FolderViewModel by viewModel()
    private val fileViewModel: FileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            category = it.getString(CATEGORY) ?: ""
            tempImagePath = it.getString(TEMP_IMAGE_PATH) ?: ""
        }
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
        val btnGoHome = v.findViewById<Button>(R.id.btnGoHome)
        val edtDocTitle = v.findViewById<EditText>(R.id.edtDocTitle)
        val ivTextClear = v.findViewById<ImageView>(R.id.ivTextClear)

        btnShare.setOnClickListener {
            dismissDialog()

        }
        btnGoHome.setOnClickListener {
            val categoryStr = CATEGORY_MAP[category]
            categoryStr?.let {
                for(folder in DEFAULT_FOLDER_LIST) {
                    if(folder.second == categoryStr) {
                        val tempFile = File(tempImagePath)
                        val newFile = File("${folder.first}/${edtDocTitle.text}.png")
                        FileUtils.copyFile(tempFile, newFile)

                        val fileItem = FileItemDTO(
                            title = edtDocTitle.text.toString(),
                            path = newFile.absolutePath,
                            category = edtDocTitle.text.toString())

                        fileViewModel.insert(fileItem)
                    }
                }
            }
            dismissDialog()
        }

        val fileNum = PP.FILE_NUM.getInt()
        val newFile = "새파일(${fileNum + 1})"
        PP.FILE_NUM.set(fileNum + 1)
        edtDocTitle.setText(newFile)

        ivTextClear.setOnClickListener {
            edtDocTitle.text.clear()
            edtDocTitle.requestFocus()
        }

        folderViewModel.foldersResponse.observe(this, Observer {
            categorySwipeView.setCategories(it, category)
        })
        folderViewModel.select()
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