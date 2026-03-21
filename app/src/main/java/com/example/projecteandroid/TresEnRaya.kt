package com.example.projecteandroid

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TresEnRaya : AppCompatActivity() {

    private lateinit var btnReiniciar: Button
    private lateinit var btnTornarMenu: Button
    private lateinit var botones: Array<Button>

    private var estadoTablero = IntArray(9)
    private var juegoActivo = true

    private val posicionesGanadoras = arrayOf(
        intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
    )

    // Variables de Firebase preparadas
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tres_en_raya)

        // Inicialitzem Firebase
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        val database = FirebaseDatabase.getInstance("https://projecteandroid-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("DATA JUGADORS")

        val tf = Typeface.createFromAsset(assets, "fonts/digitalDisco.ttf")

        btnReiniciar = findViewById(R.id.btnReiniciar)
        btnTornarMenu = findViewById(R.id.btnTornarMenu)

        btnReiniciar.typeface = tf
        btnTornarMenu.typeface = tf

        botones = arrayOf(
            findViewById(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
            findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8)
        )

        for (i in botones.indices) {
            botones[i].typeface = tf
            botones[i].setOnClickListener { botonPulsado(botones[i], i) }
        }

        btnReiniciar.setOnClickListener { reiniciarPartida() }

        btnTornarMenu.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun botonPulsado(boton: Button, posicion: Int) {
        if (!juegoActivo || estadoTablero[posicion] != 0) return

        boton.text = getString(R.string.x_text)
        estadoTablero[posicion] = 1

        if (comprobarGanador()) return

        juegaMaquina()
    }

    private fun juegaMaquina() {
        val casellesBuides = mutableListOf<Int>()
        for (i in 0..8) {
            if (estadoTablero[i] == 0) casellesBuides.add(i)
        }

        if (casellesBuides.isNotEmpty()) {
            val posMaquina = casellesBuides.random()
            botones[posMaquina].text = getString(R.string.o_text)
            estadoTablero[posMaquina] = 2

            comprobarGanador()
        }
    }

    private fun comprobarGanador(): Boolean {
        var ganador = 0

        for (posicion in posicionesGanadoras) {
            if (estadoTablero[posicion[0]] != 0 &&
                estadoTablero[posicion[0]] == estadoTablero[posicion[1]] &&
                estadoTablero[posicion[1]] == estadoTablero[posicion[2]]
            ) {
                ganador = estadoTablero[posicion[0]]
                juegoActivo = false
                break
            }
        }

        if (ganador != 0) {
            val missatge = if (ganador == 1) getString(R.string.has_guanyat) else getString(R.string.guanya_maquina)
            Toast.makeText(this, missatge, Toast.LENGTH_LONG).show()

            // Si la jugadora gana (ganador == 1), sumamos puntos
            if (ganador == 1) {
                sumarPuntuacion()
            }

            return true
        } else if (!estadoTablero.contains(0)) {
            Toast.makeText(this, getString(R.string.empat), Toast.LENGTH_LONG).show()
            juegoActivo = false
            return true
        }

        return false
    }

    // NOVA FUNCIÓ: Sumar puntuació a Firebase
    private fun sumarPuntuacion() {
        user?.let { usuario ->
            val uid = usuario.uid

            // Llegim la puntuació actual
            reference.child(uid).child("puntuacion").get().addOnSuccessListener { snapshot ->
                // Agafem el valor actual (si falla o està buit, serà 0)
                val puntuacionActual = snapshot.value.toString().toIntOrNull() ?: 0

                // Sumem 10 punts per guanyar (pots canviar aquesta xifra si vols)
                val nuevaPuntuacion = puntuacionActual + 10

                // Guardem la nova puntuació a Firebase
                reference.child(uid).child("puntuacion").setValue(nuevaPuntuacion)
                    .addOnSuccessListener {
                        Toast.makeText(this, "+10 Punts guardats a la base de dades!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun reiniciarPartida() {
        juegoActivo = true
        estadoTablero = IntArray(9)

        for (boton in botones) {
            boton.text = ""
        }
    }
}