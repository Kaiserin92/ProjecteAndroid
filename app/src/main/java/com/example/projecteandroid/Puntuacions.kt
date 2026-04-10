package com.example.projecteandroid

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Puntuacions : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JugadorsAdapter
    private val llistaJugadors = mutableListOf<Jugador>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntuacions)

        // Configurar la tipografía del título
        val titol = findViewById<TextView>(R.id.titolPuntuacions)
        val tf = Typeface.createFromAsset(assets, "fonts/digitalDisco.ttf")
        titol.typeface = tf

        // Preparar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPuntuacions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Conectamos el Adapter (al principio la lista está vacía)
        adapter = JugadorsAdapter(llistaJugadors)
        recyclerView.adapter = adapter

        // Descargar datos de Firebase
        obtenirJugadorsFirebase()
    }

    private fun obtenirJugadorsFirebase() {
        val database = FirebaseDatabase.getInstance("https://projecteandroid-default-rtdb.europe-west1.firebasedatabase.app/")
        val reference = database.getReference("DATA JUGADORS")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                llistaJugadors.clear() // Limpiamos para no duplicar si hay cambios

                // Recorremos todos los jugadores de la base de datos
                for (ds in snapshot.children) {
                    val nom = ds.child("nombre").value?.toString() ?: "Desconegut"
                    // Si no tiene puntuación, le ponemos 0
                    val puntuacio = ds.child("puntuacion").value?.toString()?.toIntOrNull() ?: 0
                    val imatge = ds.child("imatge").value?.toString() ?: "gato"

                    // Creamos el objeto Jugador y lo añadimos a la lista
                    val jugador = Jugador(nom, puntuacio, imatge)
                    llistaJugadors.add(jugador)
                }

                // BONUS: Ordenamos la lista de mayor a menor puntuación para que sea un Ránquing real
                llistaJugadors.sortByDescending { it.puntuacio }

                // Le avisamos al Adapter de que ya tenemos los datos listos para mostrar
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR_BBDD", "Error al llegir puntuacions: ${error.message}")
            }
        })
    }
}