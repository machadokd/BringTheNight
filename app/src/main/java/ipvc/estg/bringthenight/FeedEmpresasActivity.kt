package ipvc.estg.bringthenight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import ipvc.estg.bringthenight.EmpresaRecycler.PostAdapter
import ipvc.estg.bringthenight.EmpresaRecycler.PostDividerDecoration
import ipvc.estg.bringthenight.models.Evento
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_feed_empresas.*
import java.security.AccessController.getContext
import java.util.zip.Inflater

class FeedEmpresasActivity : AppCompatActivity() {

    private lateinit var events : DatabaseReference
    private lateinit var userId : String

//    EVENT LIST
    private var org_events : MutableList<Evento> = ArrayList()

//    LISTENER
    private lateinit var eventListener: ValueEventListener

    private lateinit var postAdapter: PostAdapter

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
            it.children.forEach { child ->
                val event = child.getValue(Evento::class.java)
                if (event!!.estabelecimento == userId) {
                    org_events.add(event)
                }
            }

            postAdapter = PostAdapter(this@FeedEmpresasActivity, org_events)
            var recyclerView = recycler

            recyclerView.apply {
                adapter = postAdapter
                layoutManager = LinearLayoutManager(this@FeedEmpresasActivity)
                val decoration = PostDividerDecoration(30)
                addItemDecoration(decoration)
            }

        }.addOnFailureListener{
            Log.e("firebase_event", "Error getting data", it)
        }



    }
}