package ipvc.estg.bringthenight

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_criar_evento.*
import kotlinx.android.synthetic.main.activity_criar_evento.imageView
import kotlinx.android.synthetic.main.activity_criar_evento.title_edit_text
import kotlinx.android.synthetic.main.activity_editar_evento.*
import kotlinx.android.synthetic.main.empresa_post.view.*
import java.io.File
import java.util.*

class EditarEventoActivity : AppCompatActivity() {
    
    private lateinit var db_reference : DatabaseReference
    private lateinit var evento: Evento

    private var image : Uri? = null
    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(PICK_IMAGE, result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_evento)

        var id_evento = intent.getStringExtra("evento")
        
        db_reference = FirebaseDatabase.getInstance().reference.child("events").child(id_evento!!)


        db_reference.get().addOnCompleteListener {
            evento = it.getResult()!!.getValue(Evento::class.java)!!
            Log.i("editar_evento", "evento ${evento}")
            title_edit_text.setText(evento.titulo)

            val storageRef = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")
            val localFile = File.createTempFile("temp_file", "png")
            var bitmap: Bitmap? = null

            storageRef.getFile(localFile).addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Log.i("firebase_image", "Image $it")
                imageView.setImageBitmap(bitmap!!)
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Evento não existe", Toast.LENGTH_SHORT).show()
            finish()
        }

        edit_image.setOnClickListener { load_image() }

        edit_button.setOnClickListener { edit_event() }

        

    }

    private fun load_image() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        resultLauncher.launch(gallery)

    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (result.resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            image = result.data?.data!!
            imageView.setImageURI(image)
        }
    }

    private fun edit_event() {
        val title = title_edit_text.text

        if (!title.isNullOrBlank()){

            val storageReference = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")


            evento.apply {
                titulo = title.toString()!!
            }



            db_reference.setValue(evento).addOnSuccessListener {

                if (image != null){
                    storageReference.putFile(image!!).addOnSuccessListener {
                        Toast.makeText(this, "Imagem Trocada.", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Falhou a dar upload da imagem.", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Falhou a criar evento.", Toast.LENGTH_SHORT).show()
            }

        }
        else {
            Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE = 100
    }


}