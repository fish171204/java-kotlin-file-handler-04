package com.example.baicongdiemnhom02_nguyendangkhoa_nguyentrandoanthi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.IOException
import android.database.Cursor
import android.provider.OpenableColumns
class MainActivity : AppCompatActivity() {

    private lateinit var txtFileContent: TextView
    private lateinit var imgView: ImageView
    private val PICK_FILE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLoadFile: Button = findViewById(R.id.btnLoadFile)
        val btnSaveFile: Button = findViewById(R.id.btnSaveFile)
        txtFileContent = findViewById(R.id.txtFileContent)
        imgView = findViewById(R.id.imgView)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Quản lý File"



        btnLoadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        }

        btnSaveFile.setOnClickListener {
            saveTextToFile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                handleFile(uri)
            }
        }
    }



    private fun getFileName(uri: Uri): String {
        var name = "Không xác định"
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    private fun handleFile(uri: Uri) {
        val fileType = contentResolver.getType(uri)
        val fileName = getFileName(uri)

        when {
            fileType?.startsWith("image/") == true -> {
                imgView.visibility = View.VISIBLE
                imgView.setImageURI(uri)
                txtFileContent.text = "Hình ảnh đã chọn: $fileName"
            }
            fileType == "text/plain" -> {
                imgView.visibility = View.GONE
                val text = readTextFromUri(uri)
                txtFileContent.text = "Tên file: $fileName\n\nNội dung:\n$text"
            }
            fileType == "application/vnd.ms-excel" || fileType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                imgView.visibility = View.GONE
                txtFileContent.text = "File Excel: $fileName"
            }
            else -> {
                imgView.visibility = View.GONE
                txtFileContent.text = "File không hỗ trợ: $fileName"
            }
        }
    }



    private fun readTextFromUri(uri: Uri): String {
        val content = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() ?: "" }
        println("📂 Nội dung file: $content")
        return content
    }


    private fun saveTextToFile() {
        val text = txtFileContent.text.toString()
        val fileName = "my_file.txt"
        val file = File(getExternalFilesDir(null), fileName)

        try {
            file.writeText(text)
            Toast.makeText(this, "Lưu thành công: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Lỗi khi lưu file!", Toast.LENGTH_SHORT).show()
        }

    }

}
