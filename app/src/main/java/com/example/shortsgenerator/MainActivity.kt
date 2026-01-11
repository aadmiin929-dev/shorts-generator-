inputText.addTextChangedListener {
    val count = it?.length ?: 0
    textCounter.text = "$count символов"
}

fun generateSrt(text: String): String {
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

fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val millis = ms % 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60

    return String.format(
        "%02d:%02d:%02d,%03d",
        0, minutes, seconds, millis
    )
}
