package com.example.projecteandroid

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Buscamos los botones en el diseño y los guardamos en variables
        var BTMLOGIN = findViewById<Button>(R.id.BTMLOGIN)
        var BTMREGISTRO = findViewById<Button>(R.id.BTMREGISTRO)

        // Le decimos al botón de Login qué hacer cuando lo pulsen
        BTMLOGIN.setOnClickListener {
            val intent= Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Le decimos al botón de Registro qué hacer cuando lo pulsen
        BTMREGISTRO.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }
}