package ipvc.estg.bringthenight

import android.app.Instrumentation
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_criar_evento.*
import java.util.*
import kotlin.collections.HashMap

class CriarEventoActivity : AppCompatActivity() {

    private lateinit var userId : String
    private lateinit var events : DatabaseReference
    private var image : Uri? = null

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(PICK_IMAGE, result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_evento)

        userId = FirebaseAuth.getInstance().currentUser!!.uid
        events = FirebaseDatabase.getInstance().getReference("events")

        create_button.setOnClickListener {
            create_event()
        }

        add_image.setOnClickListener {
            load_image()
        }

    }

    private fun load_image() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        resultLauncher.launch(gallery)

//        startActivityForResult(gallery, 100)
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (result.resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            image = result.data?.data!!
            imageView.setImageURI(image)
        }
    }



    private fun create_event() {
        val title = title_edit_text.text

        if (!title.isNullOrBlank() && image != null){

            val now = Date()
            val filename = now.time.toString()
            val storageReference = FirebaseStorage.getInstance().getReference("images/$userId/$filename")

            var evento = Evento(titulo = title.toString(), estabelecimento = userId, imagem = filename)


            events.push().setValue(evento).addOnSuccessListener {
                storageReference.putFile(image!!).addOnSuccessListener {
                    Toast.makeText(this, "Imagem Carregada.", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Falhou a dar upload da imagem.", Toast.LENGTH_SHORT).show()
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