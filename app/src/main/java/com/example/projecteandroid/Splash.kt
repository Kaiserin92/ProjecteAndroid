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

    private fun canviarActivity(){
        Timer().schedule(duracio){
            saltainici()
        }
    }
    fun saltainici()
    {
        // Para la muscia i allibera memoria
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        val intent=Intent(this, MainActivity::class.java)
        startActivity(intent)
        // Tanca portada que no es quedi de fons
        finish()
    }
}