package ipvc.estg.bringthenight.models

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipvc.estg.bringthenight.R
import ipvc.estg.bringthenight.UserNewChatActivity
import kotlinx.android.synthetic.main.activity_chat_window.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatWindowActivity : AppCompatActivity() {

    private var fromId : String? = null
    private var toId : String? = null
    private  var toUser : User? = null

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_window)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler_chat_window.adapter = adapter
        recycler_chat_window.layoutManager = LinearLayoutManager(this@ChatWindowActivity)

        toUser = intent.getParcelableExtra<User>(UserNewChatActivity.USER_KEY)!!
        toId = toUser!!.id
        fromId = FirebaseAuth.getInstance().currentUser!!.uid

        supportActionBar?.title = toUser!!.nome

        listenForMessages()

        send_message_button.setOnClickListener {
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("messages")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                Log.i("chat_message", "performSendMessage: $chatMessage")

                if (chatMessage != null) {
                    if (chatMessage.fromId == fromId){
                        adapter.add(ChatToItem(chatMessage.message))
                    }else if (chatMessage.toId == toId){
                        adapter.add(ChatFromItem(chatMessage.message))
                    }
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

    private fun performSendMessage() {

        val message = message_edit_text.text.toString()

        if (!message.isNullOrBlank() && fromId != null && toId != null){
            val reference = FirebaseDatabase.getInstance().getReference("messages").push()

            val chatMessage = ChatMessage(message = message, fromId = fromId!!,toId = toId!!,  timestamp = System.currentTimeMillis(), id = reference.key!!)
            reference.setValue(chatMessage).addOnSuccessListener {
                Log.i("message_sent", "performSendMessage: $it")
                message_edit_text.setText("")
            }
        }else {
            Log.i("message_sent", "error: ${message}, ${fromId}, ${toId}")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


}


class ChatFromItem(val message : String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.from_row_text.text = message
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val message : String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.to_row_text.text = message
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}

