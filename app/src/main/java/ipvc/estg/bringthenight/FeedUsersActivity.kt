package ipvc.estg.bringthenight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import ipvc.estg.bringthenight.adapters.FeedUsersAdapter
import ipvc.estg.bringthenight.entities.Event
import kotlinx.android.synthetic.main.activity_feed_users.*

class FeedUsersActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_users)

        readEventos()

    }

    private fun readEventos(){
        database = FirebaseDatabase.getInstance().getReference("events")

        val eventos : MutableList<Event> = ArrayList()
        eventos.clear()

        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Machado", "Deu erro a ir buscar os dados")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val children = p0!!.children
                children.forEach{
                    eventos.add(it.getValue(Event::class.java)!!)
                }

                RecyclerFeedUser.apply{
                    layoutManager = LinearLayoutManager(this@FeedUsersActivity)
                    adapter = FeedUsersAdapter(this@FeedUsersActivity, eventos as ArrayList<Event>)
                }


            }

        }
        database.addValueEventListener(postListener)
    }
}