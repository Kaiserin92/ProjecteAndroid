package com.example.projecteandroid

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {
    //Despleguem les variables que farem servir
    lateinit var correoLogin : EditText
    lateinit var passLogin : EditText
    lateinit var btnLogin : Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialitzem Firebase
        auth = FirebaseAuth.getInstance()

        // Busquem a R els elements als que apunten les variables
        correoLogin =findViewById<EditText>(R.id.correoLogin)
        passLogin =findViewById<EditText>(R.id.passLogin)
        btnLogin =findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            // Abans de fer el login validem les dades
            val email: String = correoLogin.text.toString()
            val passw: String = passLogin.text.toString()

            // validació del correu
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                correoLogin.error = "Invalid Mail"
            } else if (passw.length < 6) {
                passLogin.error = "Password less than 6 chars"
            } else {
                LogindeJugador(email, passw)
            }
        }
    }

    private fun LogindeJugador(email: String, passw: String) {
        auth.signInWithEmailAndPassword(email, passw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // He posat "Benvinguda" per mantenir el text en femení
                    val tx = "Benvinguda $email"
                    Toast.makeText(this, tx, Toast.LENGTH_LONG).show()

                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "ERROR Autentificació", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Mètode separat per fer el salt al Menú
    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
        finish()
    }

}