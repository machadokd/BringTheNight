package ipvc.estg.bringthenight

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipvc.estg.bringthenight.models.*
import kotlinx.android.synthetic.main.activity_chat_window.*
import kotlinx.android.synthetic.main.activity_feed_empresas.*
import kotlinx.android.synthetic.main.activity_ver_mais_evento.*
import kotlinx.android.synthetic.main.comentario.view.*
import kotlinx.android.synthetic.main.linha_feed_user.view.*
import java.io.File
import java.lang.Exception
import java.lang.NullPointerException

class VerMaisEventoActivity : AppCompatActivity() {

    private lateinit var id_evento : String
    private lateinit var id_user : String
    private val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var username : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mais_evento)

        id_user = FirebaseAuth.getInstance().uid!!
        id_evento = intent.getStringExtra("evento")!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        carregaEvento()

        toggle_like.setOnCheckedChangeListener { buttonView, isChecked ->
            if ( !buttonView.isPressed) return@setOnCheckedChangeListener
            like_event(isChecked)
        }

        recycler_comentarios.adapter = adapter
        recycler_comentarios.layoutManager = LinearLayoutManager(this@VerMaisEventoActivity)

        val button_comment = findViewById<ImageView>(R.id.button_comentar_evento)
        button_comment.setOnClickListener {
            val espaco_comentario = findViewById<EditText>(R.id.comentar_evento)
            espaco_comentario.visibility = TextView.VISIBLE
            val send_comentario = findViewById<ImageView>(R.id.send_comentario)
            send_comentario.visibility = ImageView.VISIBLE

            send_comentario.setOnClickListener {
                performSendComentario()
                espaco_comentario.visibility = TextView.GONE
                send_comentario.visibility = ImageView.GONE
            }
        }
        listenForMessages()
    }


    private fun like_event(checked: Boolean) {
        var ref = FirebaseDatabase.getInstance().getReference("gostos/${id_user}/${id_evento}")

        ref.setValue(checked).addOnCompleteListener {
            Log.i("liked_event", "like_event: liked")

            var referencia = FirebaseDatabase.getInstance().getReference("events/${id_evento}")
            referencia.get().addOnSuccessListener {
                var evento = it.getValue(Evento::class.java)

                if(checked == true) {
                    evento!!.gostos ++
                }else{
                    evento!!.gostos --
                }

                var referencia2 = FirebaseDatabase.getInstance().getReference("events/${id_evento}")
                referencia2.setValue(evento).addOnSuccessListener {
                    if (evento!!.gostos == 1) {
                        likes_descricao_evento_ver_mais.text = evento!!.gostos.toString() + " " + "like"
                    }else{
                        likes_descricao_evento_ver_mais.text = evento!!.gostos.toString() + " " + "likes"
                    }
                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }

            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

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
                    try {
                        toggle_like.isChecked = it.result.value as Boolean
                    }catch (err : NullPointerException){
                        Log.e("erro",err.toString())
                    }
                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }

            var referencia = FirebaseDatabase.getInstance().getReference("events/${id_evento}")
            referencia.get().addOnSuccessListener {
                var evento = it.getValue(Evento::class.java)

                if (evento!!.gostos == 1) {
                    likes_descricao_evento_ver_mais.text = evento!!.gostos.toString() + " " + "like"
                }else{
                    likes_descricao_evento_ver_mais.text = evento!!.gostos.toString() + " " + "likes"
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        }.addOnFailureListener { finish() }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun listenForMessages() {

        val ref = FirebaseDatabase.getInstance().getReference("events-comments/${id_evento}")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val comentario = snapshot.getValue(Comentario::class.java)
                if (comentario != null) {
                    adapter.add(adapterComentario(comentario.comentario, comentario.nome))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun performSendComentario() {
        val message = comentar_evento.text.toString()
        var nome_user: String = ""

        val refer = FirebaseDatabase.getInstance().getReference("users/${id_user}").get().addOnSuccessListener {
            var aux = it.getValue(User::class.java)
            nome_user = aux!!.nome
            if (!message.isNullOrBlank()){
                val reference = FirebaseDatabase.getInstance().getReference("events-comments/${id_evento}").push()

                val comentario = Comentario(nome_user, message)
                reference.setValue(comentario).addOnSuccessListener {
                    Log.i("message_sent", "performSendMessage: $it")
                    comentar_evento.text.clear()
                    recycler_comentarios.scrollToPosition(adapter.itemCount - 1)
                }

            }else {
                Log.i("message_sent", "error: ${message}")
            }
        }





    }

}

class adapterComentario(val com: String, val user: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.user_comentario.text = user
        viewHolder.itemView.comentario_comentario.text = com
    }

    override fun getLayout(): Int {
        return R.layout.comentario
    }

}