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
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.io.File

class Menu : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    lateinit var tancarSessio: Button
    lateinit var creditsBtn: Button
    lateinit var puntuacionsBtn: Button
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
    private val galeriaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uriResultat: Uri? ->
        uriResultat?.let { uriBona ->
            try {
                val contentResolver = applicationContext.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uriBona, takeFlags)
            } catch (e: Exception) {
                Log.e("ERROR", "No s'ha pogut demanar permís persistent: ${e.message}")
            }

            // SOLUCIÓ DEFINITIVA: Convertim a text perquè Kotlin no es queixi del tipus Uri
            Picasso.get().load(uriBona.toString()).into(imatgePerfil)

            user?.let {
                reference.child(it.uid).child("imatge").setValue(uriBona.toString())
            }
        }
    }

    // Variables i lògica per a la Càmera
    private var uriCamara: Uri? = null

    private val camaraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { exit ->
        if (exit) {
            uriCamara?.let { uriBona ->
                // SOLUCIÓ DEFINITIVA: Convertim a text perquè Kotlin no es queixi del tipus Uri
                Picasso.get().load(uriBona.toString()).into(imatgePerfil)

                user?.let {
                    reference.child(it.uid).child("imatge").setValue(uriBona.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Foto de la càmera guardada!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        } else {
            Toast.makeText(this, "S'ha cancel·lat la foto", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        val tf = Typeface.createFromAsset(assets, "fonts/digitalDisco.ttf")

        tancarSessio = findViewById(R.id.tancarSessio)
        creditsBtn = findViewById(R.id.CreditsBtn)
        puntuacionsBtn = findViewById(R.id.PuntuacionsBtn)
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

        val views: List<TextView> = listOf(miPuntuaciotxt, puntuacio, uid, correo, nom, edat, poblacio, tancarSessio, creditsBtn, puntuacionsBtn, jugarBtn, canviarImatgeBtn)
        views.forEach { it.typeface = tf }

        consulta()

        canviarImatgeBtn.setOnClickListener { mostrarDialogoImagen() }
        tancarSessio.setOnClickListener { tancalaSessio() }
        jugarBtn.setOnClickListener { startActivity(Intent(this, TresEnRaya::class.java)) }

        creditsBtn.setOnClickListener {
            Toast.makeText(this, "Credits", Toast.LENGTH_SHORT).show()
        }

        puntuacionsBtn.setOnClickListener {
            Toast.makeText(this, "Puntuacions", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obrirCamara() {
        try {
            val directori = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            val fitxer = File.createTempFile("foto_perfil_", ".jpg", directori)

            // 1. Obtenim la URI i la guardem en una variable local "val" (100% segura per a Kotlin)
            val uriSegura = FileProvider.getUriForFile(this, "${packageName}.fileprovider", fitxer)

            // 2. La guardem a la teva variable global perquè Picasso la pugui fer servir després
            uriCamara = uriSegura

            // 3. Llancem la càmera utilitzant la variable segura
            camaraLauncher.launch(uriSegura)

        } catch (e: Exception) {
            Toast.makeText(this, "Error en obrir la càmera", Toast.LENGTH_SHORT).show()
            Log.e("ERROR_CAMERA", e.message.toString())
        }
    }

    private fun mostrarDialogoImagen() {
        val opciones = arrayOf("Escollir de la Galeria", "Fer una foto")
        AlertDialog.Builder(this).setTitle("Canviar Imatge").setItems(opciones) { _, quin ->
            when (quin) {
                0 -> galeriaLauncher.launch("image/*")
                1 -> obrirCamara()
            }
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

                        val imatgeUrl = ds.child("imatge").value.toString()
                        if (imatgeUrl == "gato") {
                            Picasso.get().load(R.drawable.gato).into(imatgePerfil)
                        } else if (imatgeUrl.isNotEmpty()) {
                            Picasso.get()
                                .load(imatgeUrl.toUri())
                                .placeholder(R.drawable.gato)
                                .error(R.drawable.gato)
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