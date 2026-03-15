package com.example.projecteandroid

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import kotlin.concurrent.schedule

class Splash : AppCompatActivity() {
    private val duracio: Long=5000;
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_splash)

        //amaguem la barra, pantalla a full
        supportActionBar?.hide()
        // 1. Carreguem l'arxiu de la carpeta raw
        mediaPlayer = MediaPlayer.create(this, R.raw.music_splash)
        // 2. Inicia la musica
        mediaPlayer?.start()
        canviarActivity();
    }

    private fun canviarActivity() {
        // Usamos un Handler para que el salto se haga de forma segura
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            saltainici()
        }, duracio)
    }

    private fun saltainici() {
        // Para la música y libera memoria
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        // Ahora el Intent no fallará porque estamos en el hilo correcto
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Cerramos la portada
        finish()
    }
}