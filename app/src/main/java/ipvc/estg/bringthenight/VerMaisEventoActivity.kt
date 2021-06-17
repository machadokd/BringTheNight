package ipvc.estg.bringthenight

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class VerMaisEventoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mais_evento)

        carregaEvento()

        var like_button = findViewById<ImageView>(R.id.likes_evento_ver_mais)
        like_button.setOnClickListener {
            dalike(like_button)
        }
    }

    private fun carregaEvento() {
        var imagem = intent.getByteArrayExtra("bytearray")
        var bitmap = BitmapFactory.decodeByteArray(imagem, 0,imagem!!.size)

        var img = findViewById<ImageView>(R.id.imagem_evento_ver_mais)
        img.setImageBitmap(bitmap)

        var id_user = intent.getStringExtra("id_user")
        var id_evento = intent.getStringExtra("id_evento")

        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        ref.child("eventos").child(id_evento!!).child("gostos").get().addOnSuccessListener {
            if(it.hasChild(id_user!!)){
                var like = findViewById<ImageView>(R.id.likes_evento_ver_mais)
                like.setBackgroundResource(R.drawable.ic_liked)
            }
        }.addOnFailureListener {

        }
    }

    private fun dalike(like : ImageView){
        var id_user = intent.getStringExtra("id_user")
        var id_evento = intent.getStringExtra("id_evento")

        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference()
        ref.child("events").child(id_evento!!).get().addOnSuccessListener {
            if(it.hasChild("gostos")){
                Log.e("eventos", "existe")
            }else{

                var hashMap : HashMap<String, Any> = HashMap()

                hashMap.put("id", id_user!!)

                val ref : DatabaseReference =  FirebaseDatabase.getInstance().getReference().child("events").child(id_evento!!).child("gostos").child(id_user!!)

                ref.setValue(hashMap).addOnSuccessListener{

                    Log.e("eventos", "sucesso")
                    like.setBackgroundResource(R.drawable.ic_liked)

                }.addOnFailureListener {
                    Log.e("eventos", "insucesso")
                }
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }
}