package ipvc.estg.bringthenight

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_ver_mais_evento.*
import kotlinx.android.synthetic.main.linha_feed_user.view.*
import java.io.File

class VerMaisEventoActivity : AppCompatActivity() {

    private lateinit var id_evento : String
    private lateinit var id_user : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mais_evento)

        id_user = FirebaseAuth.getInstance().uid!!
        id_evento = intent.getStringExtra("evento")!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        carregaEvento()

//        var like_button = findViewById<ImageView>(R.id.likes_evento_ver_mais)
//        like_button.setOnClickListener {
//            dalike(like_button)
//        }

        toggle_like.setOnCheckedChangeListener { buttonView, isChecked ->
            like_event(isChecked)
        }

    }

    private fun like_event(checked: Boolean) {
        var ref = FirebaseDatabase.getInstance().getReference("gostos/${id_user}/${id_evento}")

        ref.setValue(checked).addOnCompleteListener {
            Log.i("liked_event", "like_event: liked")
        }.addOnFailureListener {  }

    }

    private fun carregaEvento() {

        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        ref.child("events").child(id_evento!!).get().addOnCompleteListener {
            val evento = it.result.getValue(Evento::class.java) ?: return@addOnCompleteListener
            FirebaseStorage.getInstance().getReference()

            val storageRef = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")
            val localFile = File.createTempFile("temp_file", "png")
            var bitmap: Bitmap? = null

            storageRef.getFile(localFile).addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Log.i("bitmapPerfil", "bitmapPerfil ${bitmap}")
                imagem_evento_ver_mais.setImageBitmap(bitmap!!)
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

            var like_ref = FirebaseDatabase.getInstance().getReference("gostos/${id_user}/${id_evento}")
            like_ref.get().addOnCompleteListener {
                toggle_like.isChecked = it.result.value as Boolean
            }



        }.addOnFailureListener { finish() }


//        ref.child("eventos").child(id_evento!!).child("gostos").get().addOnSuccessListener {
//            if(it.hasChild(id_user!!)){
//                var like = findViewById<ImageView>(R.id.likes_evento_ver_mais)
//                like.setBackgroundResource(R.drawable.ic_liked)
//            }
//        }.addOnFailureListener {
//
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

//    private fun dalike(like : ImageView){
//        var id_user = intent.getStringExtra("id_user")
//        var id_evento = intent.getStringExtra("id_evento")
//
//        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference()
//        ref.child("events").child(id_evento!!).get().addOnSuccessListener {
//            if(it.hasChild("gostos")){
//                Log.e("eventos", "existe")
//            }else{
//
//                var hashMap : HashMap<String, Any> = HashMap()
//
//                hashMap.put("id", id_user!!)
//
//                val ref : DatabaseReference =  FirebaseDatabase.getInstance().getReference().child("events").child(id_evento!!).child("gostos").child(id_user!!)
//
//                ref.setValue(hashMap).addOnSuccessListener{
//
//                    Log.e("eventos", "sucesso")
//                    like.setBackgroundResource(R.drawable.ic_liked)
//
//                }.addOnFailureListener {
//                    Log.e("eventos", "insucesso")
//                }
//            }
//
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }
//
//    }
}