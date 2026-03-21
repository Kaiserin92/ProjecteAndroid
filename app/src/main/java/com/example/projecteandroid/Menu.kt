package com.example.projecteandroid

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class Menu : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    lateinit var tancarSessio: Button
    lateinit var CreditsBtn: Button
    lateinit var PuntuacionsBtn: Button
    lateinit var jugarBtn: Button
    lateinit var canviarImatgeBtn: Button

    lateinit var miPuntuaciotxt: TextView
    lateinit var puntuacio: TextView
    lateinit var uid: TextView
    lateinit var correo: TextView
    lateinit var nom: TextView
    lateinit var edat: TextView
    lateinit var poblacio: TextView
    lateinit var imatgePerfil: ImageView

    // Lògica per la Galeria
    private val galeriaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Intentem demanar permís persistent per a aquesta URI (perquè no s'esborri en tancar l'app)
            try {
                val contentResolver = applicationContext.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                Log.e("ERROR", "No s'ha pogut demanar permís persistent")
            }

            Picasso.get().load(uri).into(imatgePerfil)

            user?.let {
                reference.child(it.uid).child("imatge").setValue(uri.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        val tf = Typeface.createFromAsset(assets, "fonts/digitalDisco.ttf")

        // Enllaçar vistes
        tancarSessio = findViewById(R.id.tancarSessio)
        CreditsBtn = findViewById(R.id.CreditsBtn)
        PuntuacionsBtn = findViewById(R.id.PuntuacionsBtn)
        jugarBtn = findViewById(R.id.jugarBtn)
        canviarImatgeBtn = findViewById(R.id.canviarImatgeBtn)
        miPuntuaciotxt = findViewById(R.id.miPuntuaciotxt)
        puntuacio = findViewById(R.id.puntuacio)
        uid = findViewById(R.id.uid)
        correo = findViewById(R.id.correo)
        nom = findViewById(R.id.nom)
        edat = findViewById(R.id.edat)
        poblacio = findViewById(R.id.poblacio)
        imatgePerfil = findViewById(R.id.alienimagen)

        // Tipografia
        val views = listOf(miPuntuaciotxt, puntuacio, uid, correo, nom, edat, poblacio, tancarSessio, CreditsBtn, PuntuacionsBtn, jugarBtn, canviarImatgeBtn)
        views.forEach { (it as? TextView)?.typeface = tf }

        consulta()

        canviarImatgeBtn.setOnClickListener { mostrarDialogoImagen() }
        tancarSessio.setOnClickListener { tancalaSessio() }
        jugarBtn.setOnClickListener { startActivity(Intent(this, TresEnRaya::class.java)) }
    }

    private fun mostrarDialogoImagen() {
        val opciones = arrayOf("Escollir de la Galeria", "Fer una foto (Properament)")
        AlertDialog.Builder(this).setTitle("Canviar Imatge").setItems(opciones) { _, quin ->
            if (quin == 0) galeriaLauncher.launch("image/*")
        }.show()
    }

    private fun consulta() {
        val database = FirebaseDatabase.getInstance("https://projecteandroid-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("DATA JUGADORS")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    if (ds.child("correo").value.toString() == user?.email) {
                        puntuacio.text = ds.child("puntuacion").value.toString()
                        uid.text = ds.child("uid").value.toString()
                        correo.text = ds.child("correo").value.toString()
                        nom.text = ds.child("nombre").value.toString()
                        edat.text = ds.child("edat").value?.toString() ?: ""
                        poblacio.text = ds.child("poblacio").value?.toString() ?: ""

                        // CARREGAR IMATGE SI EXISTEIX
                        val imatgeUrl = ds.child("imatge").value.toString()
                        if (imatgeUrl.isNotEmpty()) {
                            // Fem servir Picasso per carregar la ruta desada
                            Picasso.get()
                                .load(Uri.parse(imatgeUrl))
                                .placeholder(R.drawable.gato) // Imatge mentre carrega
                                .error(R.drawable.gato)       // Imatge si hi ha error
                                .into(imatgePerfil)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun tancalaSessio() {
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}