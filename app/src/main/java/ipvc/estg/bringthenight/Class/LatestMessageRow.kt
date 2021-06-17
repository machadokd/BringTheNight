package ipvc.estg.bringthenight.Class

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipvc.estg.bringthenight.R
import ipvc.estg.bringthenight.models.ChatMessage
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>(){
    var chatPartnerUser : User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latest_message_text_view.text = chatMessage.message

        val chatPartnerID : String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerID = chatMessage.toId
        }
        else {
            chatPartnerID = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("users/$chatPartnerID")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java) ?: return
                viewHolder.itemView.latest_username_text_view.text = chatPartnerUser?.nome
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

}