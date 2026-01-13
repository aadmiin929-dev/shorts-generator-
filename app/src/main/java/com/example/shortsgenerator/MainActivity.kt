package com.example.shortsgenerator

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // SplashScreen — СТРОГО ПЕРВОЙ
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ---- VIEWS ----
        val inputText = findViewById<EditText>(R.id.inputText)
        val textCounter = findViewById<TextView>(R.id.textCounter)
        val resultText = findViewById<TextView>(R.id.resultText)

        val btnGenerate = findViewById<Button>(R.id.btnSrt)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // ---- COUNTER ----
        inputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textCounter.text = "${s?.length ?: 0} символов"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ---- GENERATE SRT ----
        btnGenerate.setOnClickListener {
            val text = inputText.text.toString().trim()
            if (text.isEmpty()) {
                toast("Вставь текст")
                return@setOnClickListener
            }
            resultText.text = generateSrt(text)
        }

        // ---- SAVE ----
        btnSave.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isBlank()) {
                toast("Нет SRT для сохранения")
                return@setOnClickListener
            }
            saveSrtToFile(text)
        }

        // ---- SHARE ----
        btnShare.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isBlank()) {
                toast("Сначала создай SRT")
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, "Поделиться SRT"))
        }
    }

    // ---------- LOGIC ----------

    private fun generateSrt(text: String): String {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val sb = StringBuilder()
        var startMs = 0

        lines.forEachIndexed { index, line ->
            val endMs = startMs + 1500
            sb.append(index + 1).append("\n")
            sb.append(formatTime(startMs)).append(" --> ").append(formatTime(endMs)).append("\n")
            sb.append(line).append("\n\n")
            startMs = endMs
        }
        return sb.toString()
    }

    private fun formatTime(ms: Int): String {
        val s = ms / 1000
        return String.format("00:%02d:%02d,%03d", s / 60, s % 60, ms % 1000)
    }

    private fun saveSrtToFile(text: String) {
        try {
            val file = File(getExternalFilesDir(null), "subtitles_${System.currentTimeMillis()}.srt")
            file.writeText(text)
            toast("Сохранено:\n${file.absolutePath}")
        } catch (e: Exception) {
            toast("Ошибка сохранения")
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
