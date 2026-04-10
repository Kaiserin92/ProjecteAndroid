package com.example.projecteandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class JugadorsAdapter(private val llistaJugadors: List<Jugador>) : RecyclerView.Adapter<JugadorsAdapter.JugadorsViewHolder>() {

    class JugadorsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivJugador: ImageView = view.findViewById(R.id.ivJugador)
        val tvNomJugador: TextView = view.findViewById(R.id.tvNom_Jugador)
        val tvPuntuacioJugador: TextView = view.findViewById(R.id.tvPuntuacio_Jugador)

        // Esta función "pinta" los datos del jugador en la fila
        fun render(jugador: Jugador) {
            tvNomJugador.text = jugador.nom_jugador
            tvPuntuacioJugador.text = jugador.puntuacio.toString()

            try {
                if (jugador.foto == "gato") {
                    // Cargamos el gatito ajustado
                    Picasso.get()
                        .load(R.drawable.gato)
                        .fit().centerCrop()
                        .into(ivJugador)
                } else if (jugador.foto.isNotEmpty()) {
                    // CARGAMOS LA FOTO DE LA CÁMARA REDIMENSIONADA (VITAL)
                    Picasso.get()
                        .load(jugador.foto)
                        .fit()          // <--- ESTO EVITA QUE LA APP EXPLOTE
                        .centerCrop()   // <--- ESTO LA RECORTA BONITA
                        .placeholder(R.drawable.gato)
                        .error(R.drawable.gato)
                        .into(ivJugador)
                } else {
                    ivJugador.setImageResource(R.drawable.gato)
                }
            } catch (e: Exception) {
                ivJugador.setImageResource(R.drawable.gato)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_jugador, parent, false)
        return JugadorsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return llistaJugadors.size
    }

    override fun onBindViewHolder(holder: JugadorsViewHolder, position: Int) {
        val item = llistaJugadors[position]
        holder.render(item)
    }
}