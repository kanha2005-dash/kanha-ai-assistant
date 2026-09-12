package com.kanha.ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.*
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var chatText: TextView
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 40, 24, 24)
            setBackgroundColor(0xFF080808.toInt())
        }

        val title = TextView(this).apply {
            text = "✦ KANHA AI"
            textSize = 28f
            setTextColor(0xFFD9A441.toInt())
        }

        root.addView(title)

        chatText = TextView(this).apply {
            text = """
                Namaste! 🙏

                Main Kanha hoon.
                Aap Hindi, Odia ya English mein mujhse baat kar sakte hain.
            """.trimIndent()

            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 40, 0, 30)
        }

        root.addView(
            chatText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        input = EditText(this).apply {
            hint = "Kanha se kuch poochiye..."
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF999999.toInt())
        }

        root.addView(input)

        val buttonRow = LinearLayout(this)

        val sendButton = Button(this).apply {
            text = "SEND"
            setOnClickListener {
                sendMessage()
            }
        }

        val voiceButton = Button(this).apply {
            text = "🎙 VOICE"
            setOnClickListener {
                startVoiceRecognition()
            }
        }

        buttonRow.addView(
            sendButton,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        buttonRow.addView(
            voiceButton,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        root.addView(buttonRow)

        setContentView(root)

        tts = TextToSpeech(this, this)

        if (
            checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }
    }

    private fun sendMessage() {
        val message = input.text.toString().trim()

        if (message.isEmpty()) return

        val response =
            "Aapne kaha:\n$message\n\n" +
            "Kanha AI backend connect hone ke baad " +
            "main intelligent AI response dunga."

        chatText.text = response

        speak(response)

        input.text.clear()
    }

    private fun startVoiceRecognition() {

        if (!android.speech.SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                "Speech recognition available nahi hai.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "hi-IN"
            )

            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Kanha ko boliye..."
            )
        }

        startActivityForResult(intent, 200)
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == 200 &&
            resultCode == RESULT_OK
        ) {

            val result =
                data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )?.firstOrNull()

            if (!result.isNullOrBlank()) {
                input.setText(result)
                sendMessage()
            }
        }
    }

    private fun speak(text: String) {

        if (::tts.isInitialized) {
            tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "kanha-response"
            )
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            tts.language = Locale(
                "hi",
                "IN"
            )
        }
    }

    override fun onDestroy() {

        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        super.onDestroy()
    }
}
