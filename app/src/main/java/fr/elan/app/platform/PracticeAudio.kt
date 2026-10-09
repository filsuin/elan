package fr.elan.app.platform

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import fr.elan.app.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Uses only voices declared offline by the installed engine; no recorded media or cloud API. */
class PracticeAudio(context: Context) {
    private val _voiceAvailable = MutableStateFlow(false)
    val voiceAvailable = _voiceAvailable.asStateFlow()
    private var engine: TextToSpeech? = null
    private var tone: ToneGenerator? = null
    private var closed = false
    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            if (!closed && status == TextToSpeech.SUCCESS) {
                runCatching {
                    val local = engine?.voices?.filter {
                        it.locale.language == "fr" && !it.isNetworkConnectionRequired && "notInstalled" !in (it.features ?: emptySet())
                    }?.sortedByDescending { it.quality }?.firstOrNull()
                    if (local != null) {
                        _voiceAvailable.value = engine?.setVoice(local) == TextToSpeech.SUCCESS
                        engine?.setSpeechRate(0.95f)
                    }
                }
            }
        }
    }
    fun cue(player: PracticePlayer, settings: UserSettings) {
        if (closed) return
        if (settings.soundEnabled) runCatching {
            if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
            tone?.startTone(if (player.finished) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_BEEP, 130)
        }
        if (settings.voiceEnabled && _voiceAvailable.value) {
            val text = when {
                player.finished -> "La pause est terminée. Reviens doucement."
                player.step.kind == StepKind.TRANSITION -> player.step.instruction
                else -> player.step.title
            }
            runCatching { engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "elan-practice") }
        }
    }
    fun stop() { runCatching { engine?.stop() }; runCatching { tone?.stopTone() } }
    fun close() { closed = true; stop(); engine?.shutdown(); tone?.release(); engine = null; tone = null }
}

