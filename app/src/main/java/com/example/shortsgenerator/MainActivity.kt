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

    private lateinit var inputText: EditText
    private lateinit var textCounter: TextView
    private lateinit var resultText: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnSave: Button
    private lateinit var btnShare: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        // SplashScreen — ОДИН раз
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views
        inputText = findViewById(R.id.inputText)
        textCounter = findViewById(R.id.textCounter)
        resultText = findViewById(R.id.resultText)
        btnGenerate = findViewById(R.id.btnSrt)
        btnSave = findViewById(R.id.btnSave)
        btnShare = findViewById(R.id.btnShare)

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
                toast("Вставь текст")
                return@setOnClickListener
            }
            resultText.text = generateSrt(text)
        }

        // Сохранение
        btnSave.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isBlank()) {
                toast("Нет SRT для сохранения")
                return@setOnClickListener
            }
            saveSrt(text)
        }

        // Поделиться
        btnShare.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isBlank()) {
                toast("Сначала создай SRT")
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(intent, "Поделиться SRT"))
        }
    }

    // ===== SRT =====

    private fun generateSrt(text: String): String {
        val lines = text.lines().filter { it.isNotBlank() }
        val sb = StringBuilder()
        var start = 0

        lines.forEachIndexed { index, line ->
            val end = start + 1500
            sb.append(index + 1).append("\n")
            sb.append(formatTime(start)).append(" --> ").append(formatTime(end)).append("\n")
            sb.append(line).append("\n\n")
            start = end
        }
        return sb.toString()
    }

    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        val min = sec / 60
        return String.format("00:%02d:%02d,%03d", min, sec % 60, ms % 1000)
    }

    private fun saveSrt(text: String) {
        try {
            val file = File(getExternalFilesDir(null), "subtitles_${System.currentTimeMillis()}.srt")
            file.writeText(text)
            toast("Сохранено:\n${file.name}")
        } catch (e: Exception) {
            toast("Ошибка сохранения")
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
