package com.example.projecteandroid

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Menu : AppCompatActivity() {
    // Variables per comprovar usuari i autentificació
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null

    // Variable per apuntar a la base de dades
    lateinit var reference: DatabaseReference

    // Variables pels botons
    lateinit var tancarSessio: Button
    lateinit var CreditsBtn: Button
    lateinit var PuntuacionsBtn: Button
    lateinit var jugarBtn: Button

    // Variables pels textos
    lateinit var miPuntuaciotxt: TextView
    lateinit var puntuacio: TextView
    lateinit var uid: TextView
    lateinit var correo: TextView
    lateinit var nom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        // Creem el tipus de lletra
        val tf = Typeface.createFromAsset(assets, "fonts/digitalDisco.ttf")

        // Enllacem les variables amb els botons del disseny
        tancarSessio = findViewById(R.id.tancarSessio)
        CreditsBtn = findViewById(R.id.CreditsBtn)
        PuntuacionsBtn = findViewById(R.id.PuntuacionsBtn)
        jugarBtn = findViewById(R.id.jugarBtn)

        // Busquem els textos al disseny
        miPuntuaciotxt = findViewById(R.id.miPuntuaciotxt)
        puntuacio = findViewById(R.id.puntuacio)
        uid = findViewById(R.id.uid)
        correo = findViewById(R.id.correo)
        nom = findViewById(R.id.nom)

        // Assignem el tipus de lletra als textos
        miPuntuaciotxt.typeface = tf
        puntuacio.typeface = tf
        uid.typeface = tf
        correo.typeface = tf
        nom.typeface = tf

        // Assignem el tipus de lletra als botons
        tancarSessio.typeface = tf
        CreditsBtn.typeface = tf
        PuntuacionsBtn.typeface = tf
        jugarBtn.typeface = tf

        // Cridem a la funció que buscarà les dades a Firebase
        consulta()

        // Afegim els listeners
        tancarSessio.setOnClickListener {
            tancalaSessio()
        }

        CreditsBtn.setOnClickListener {
            Toast.makeText(this, "Credits", Toast.LENGTH_SHORT).show()
        }

        PuntuacionsBtn.setOnClickListener {
            Toast.makeText(this, "Puntuacions", Toast.LENGTH_SHORT).show()
        }

        jugarBtn.setOnClickListener {
            Toast.makeText(this, "JUGAR", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        usuariLogejat()
        super.onStart()
    }

    private fun usuariLogejat() {
        if (user != null) {
            Toast.makeText(this, "Jugadora logejada", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Funció per tancar la sessió
    private fun tancalaSessio() {
        auth.signOut()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // NOVA FUNCIÓ: Consulta a Firebase
    private fun consulta() {
        // Fem servir el teu enllaç real i la teva carpeta "DATA JUGADORS"
        val database = FirebaseDatabase.getInstance("https://projecteandroid-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("DATA JUGADORS")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var trobat = false

                // Recorrem tots els fills de la base de dades
                for (ds in snapshot.children) {
                    // Mirem si el correu ("correo") coincideix amb el de la jugadora actual
                    if (ds.child("correo").value.toString() == user?.email) {
                        trobat = true

                        // Carreguem els textos amb els noms exactes que vas posar al Registre
                        puntuacio.text = ds.child("puntuacion").value.toString()
                        uid.text = ds.child("uid").value.toString()
                        correo.text = ds.child("correo").value.toString()
                        nom.text = ds.child("nombre").value.toString()
                    }
                }

                if (!trobat) {
                    Log.e("ERROR", "ERROR NO TROBAT MAIL")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", "ERROR DATABASE CANCEL: ${error.message}")
            }
        })
    }
}