package com.example.shortsgenerator

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // SplashScreen (Android 12+)
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views
        val inputText = findViewById<EditText>(R.id.inputText)
        val textCounter = findViewById<TextView>(R.id.textCounter)
        val resultText = findViewById<TextView>(R.id.resultText)
        val btnGenerate = findViewById<Button>(R.id.btnSrt)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // Счётчик символов
        inputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textCounter.text = "${s?.length ?: 0} символов"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Генерация SRT
        btnGenerate.setOnClickListener {
            val text = inputText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Вставь текст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            resultText.text = generateSrt(text)
        }

        // Сохранение
        btnSave.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Нет SRT для сохранения", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveSrtToFile(text)
        }

        // Поделиться
        btnShare.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Сначала создай SRT", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, "Поделиться SRT"))
        }
    }

    // ===== SRT =====

    private fun generateSrt(text: String): String {
        val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        val sb = StringBuilder()
        var startMs = 0
        val durationMs = 1500

        lines.forEachIndexed { index, line ->
            val endMs = startMs + durationMs
            sb.append(index + 1).append("\n")
            sb.append(formatTime(startMs)).append(" --> ").append(formatTime(endMs)).append("\n")
            sb.append(line).append("\n\n")
            startMs = endMs
        }
        return sb.toString()
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val millis = ms % 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        return String.format("%02d:%02d:%02d,%03d", 0, minutes, seconds, millis)
    }

    private fun saveSrtToFile(text: String) {
        try {
            val file = File(getExternalFilesDir(null), "subtitles_${System.currentTimeMillis()}.srt")
            file.writeText(text)
            Toast.makeText(this, "Сохранено:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
        }
    }
}
