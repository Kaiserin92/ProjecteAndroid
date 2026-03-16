package com.example.projecteandroid

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    // Variables per comprovar si la sessió està inicialitzada
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Assignem valor a les variables de Firebase
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        // Creem el tipus de lletra
        val tf = Typeface.createFromAsset(assets, "fonts/digitalDisco.ttf")

        // Busquem els botons en el disseny i els guardem en variables
        val BTMLOGIN = findViewById<Button>(R.id.BTMLOGIN)
        val BTMREGISTRO = findViewById<Button>(R.id.BTMREGISTRO)

        // Assignem el tipus de lletra als botons
        BTMLOGIN.typeface = tf
        BTMREGISTRO.typeface = tf

        BTMLOGIN.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        BTMREGISTRO.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        usuariLogejat()
        super.onStart()
    }

    // si l'usuari ja té la sessió oberta, el portem directa al Menú
    private fun usuariLogejat() {
        if (user != null) {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish() // Tanquem la MainActivity
        }
    }
}