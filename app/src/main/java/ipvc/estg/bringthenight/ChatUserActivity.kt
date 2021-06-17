package ipvc.estg.bringthenight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipvc.estg.bringthenight.Class.LatestMessageRow
import ipvc.estg.bringthenight.models.ChatMessage
import ipvc.estg.bringthenight.models.ChatWindowActivity
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_chat_user.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class ChatUserActivity : AppCompatActivity() {

    private var adapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        new_chat_button.setOnClickListener {
            val intent = Intent(this@ChatUserActivity, UserNewChatActivity::class.java)
            startActivity(intent)
        }

        recycler_latest_message.adapter = adapter
        recycler_latest_message.layoutManager = LinearLayoutManager(this@ChatUserActivity)

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatWindowActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(UserNewChatActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessages()


    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?:return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?:return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
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