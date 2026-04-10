package com.example.projecteandroid

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import java.util.Timer
import java.util.TimerTask

class Credits : AppCompatActivity() {

    // Definim el temporitzador
    var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        // Botó per tornar al Menú
        val btnTornarMenu = findViewById<Button>(R.id.btnTornarMenu)
        btnTornarMenu.setOnClickListener {
            finish() // Tanca la pantalla de crèdits i torna al menú
        }

        timer.schedule(TimeTask(), 0L, 3000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    // La classe interna que intercanvia els fragments
    private inner class TimeTask : TimerTask() {
        private var numeroFragment: Int = 2 // Comencem a 2 perquè al sumar passi al 1

        override fun run() {
            numeroFragment++
            if (numeroFragment > 2) numeroFragment = 1

            runOnUiThread {
                if (numeroFragment == 1) {
                    supportFragmentManager.commit {
                        replace(R.id.frameContainer, FragmentCentro())
                        setReorderingAllowed(true)
                    }
                } else {
                    supportFragmentManager.commit {
                        replace(R.id.frameContainer, FragmentDesarrolladora())
                        setReorderingAllowed(true)
                    }
                }
            }
        }
    }
}