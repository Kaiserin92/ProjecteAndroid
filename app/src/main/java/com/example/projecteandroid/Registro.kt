package com.example.projecteandroid

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {

    // 1. Declaramos la variable de Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

// 1. Añadimos esta línea para que Android encuentre el TextView
        val fechaTxt = findViewById<TextView>(R.id.fechaEt)

        val date = java.util.Calendar.getInstance().time

        // El tutorial usa SimpleDateFormat.getDateInstance()
        val formatter = java.text.DateFormat.getDateInstance()

        val formatedDate = formatter.format(date)

        // Ahora ya puedes usar fechaTxt porque lo hemos definido arriba
        fechaTxt.setText(formatedDate)

        // 2. Inicializamos Firebase
        auth = FirebaseAuth.getInstance()

        // Referenciamos los elementos del diseño
        val correoEt = findViewById<EditText>(R.id.correoEt)
        val passEt = findViewById<EditText>(R.id.passEt)
        val registrar = findViewById<Button>(R.id.registrar)

        registrar.setOnClickListener {
            val email: String = correoEt.text.toString()
            val pass: String = passEt.text.toString()

            // Validación
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                correoEt.error = "Invalid Mail"
            } else if (pass.length < 6) {
                passEt.error = "Password less than 6 chars"
            } else {
                registrarJugador(email, pass)
            }
        }
    }

    private fun registrarJugador(email: String, passw: String) {
        auth.createUserWithEmailAndPassword(email, passw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usuaria creada correctamente", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error en el registro: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // 1. Recogemos los datos de la pantalla
            val nombreEt = findViewById<EditText>(R.id.nombreEt)
            val fechaTxt = findViewById<TextView>(R.id.fechaEt)

            val uidString = user.uid // El ID único que le da Firebase
            val nombreString = nombreEt.text.toString()
            val fechaString = fechaTxt.text.toString()
            val correoString = user.email.toString()
            val puntuacion = 0

            // 2. CREAMOS EL HASHMAP (Página 43 del tutorial)
            // El HashMap es como una bolsa donde metemos los datos con una "etiqueta"
            val datosUsuario = HashMap<String, Any>()
            datosUsuario["uid"] = uidString
            datosUsuario["nombre"] = nombreString
            datosUsuario["fecha"] = fechaString
            datosUsuario["correo"] = correoString
            datosUsuario["puntuacion"] = puntuacion

            // 3. ENVIAMOS A REALTIME DATABASE
            val database = FirebaseDatabase.getInstance("https://projecteandroid-default-rtdb.europe-west1.firebasedatabase.app/")
            val reference = database.getReference("DATA JUGADORS") // Nombre de la carpeta en la BBDD

            reference.child(uidString).setValue(datosUsuario)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Datos guardados en la BBDD", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@Registro, Menu::class.java)
                        startActivity(intent)
                        finish()
                        // ------------------------------------------

                    } else {
                        Toast.makeText(this, "Error BBDD: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}