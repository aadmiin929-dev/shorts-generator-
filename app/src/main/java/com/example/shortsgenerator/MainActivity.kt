package com.example.shortsgenerator
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.inputText)
        val textCounter = findViewById<TextView>(R.id.textCounter)
        val btnGenerate = findViewById<Button>(R.id.btnSrt)
        val resultText = findViewById<TextView>(R.id.resultText)

        // 🔢 Счётчик символов
        inputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val count = s?.length ?: 0
                textCounter.text = "$count символов"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 🎬 Генерация SRT
        btnGenerate.setOnClickListener {
            val text = inputText.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(this, "Вставь текст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val srt = generateSrt(text)
            resultText.text = srt
        }
    }

    // ===== SRT GENERATOR =====

    private fun generateSrt(text: String): String {
        val lines = text
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val sb = StringBuilder()
        var startMs = 0
        val durationMs = 1500 // 1.5 сек на строку

        lines.forEachIndexed { index, line ->
            val endMs = startMs + durationMs

            sb.append(index + 1).append("\n")
            sb.append(formatTime(startMs))
                .append(" --> ")
                .append(formatTime(endMs))
                .append("\n")
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

        return String.format(
            "%02d:%02d:%02d,%03d",
            0, minutes, seconds, millis
        )
    }
}
