package ipvc.estg.bringthenight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import ipvc.estg.bringthenight.models.Evento
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_feed_empresas.*

class FeedEmpresasActivity : AppCompatActivity() {

    private lateinit var events : DatabaseReference
    private lateinit var userId : String

//    EVENT LIST
    private var org_events : MutableList<Evento> = ArrayList()

//    LISTENER
    private lateinit var eventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_empresas)

        new_post.setOnClickListener {
            val intent = Intent(this@FeedEmpresasActivity, CriarEventoActivity::class.java)
            startActivity(intent)
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        events = FirebaseDatabase.getInstance().reference.child("events")


        events.get().addOnSuccessListener {
            Log.i("firebase_event", "Got value ${it.value }")

            it.children.forEach { child ->
                Log.i("firebase_event", "Got child ${child.getValue(Evento::class.java)}")
                val event = child.getValue(Evento::class.java)
                if (event!!.estabelecimento == userId) {
                    Log.i("events", "Add events ${event}")
                    org_events.add(event)
                }
            }

            Log.i("events", "events ${org_events}")

        }.addOnFailureListener{
            Log.e("firebase_event", "Error getting data", it)
        }


    }
}