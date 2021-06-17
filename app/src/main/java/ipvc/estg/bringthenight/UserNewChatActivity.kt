package ipvc.estg.bringthenight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipvc.estg.bringthenight.models.ChatWindowActivity
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_user_new_chat.*
import kotlinx.android.synthetic.main.new_chat_row.view.*


class UserNewChatActivity : AppCompatActivity() {

    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_new_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userID = FirebaseAuth.getInstance().currentUser!!.uid

        fetchUsers()

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("users")

        ref.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    Log.i("snapshot_children", "onDataChange: ${it.toString()}")
                    val user = it.getValue(User::class.java)
                    if (user != null && user.id != userID)  {
                        adapter.add(NewChat(user))

                        adapter.setOnItemClickListener { item, view ->
                            val userItem = item as NewChat
                            val intent = Intent(view.context, ChatWindowActivity::class.java)
                            intent.putExtra(USER_KEY, userItem.user)
                            startActivity(intent)
                            finish()
                        }

                    }

                }
                recycler_new_message.adapter = adapter
                recycler_new_message.layoutManager = LinearLayoutManager(this@UserNewChatActivity)

            }


            override fun onCancelled(error: DatabaseError) {
                Log.i("snapshot_children", "onCancelled: ${error}")
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}


class NewChat(val user : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_text_view.text = user.nome

    }

    override fun getLayout(): Int {
        return R.layout.new_chat_row
    }

}