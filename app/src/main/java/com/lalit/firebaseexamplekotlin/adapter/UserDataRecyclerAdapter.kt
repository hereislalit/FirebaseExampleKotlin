package com.lalit.firebaseexamplekotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lalit.firebaseexamplekotlin.R
import com.lalit.firebaseexamplekotlin.model.User
import java.util.*

class UserDataRecyclerAdapter(var userList: ArrayList<User>, var listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(user: User)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        var user = userList!![position]
        if (viewHolder is ViewHolder) {
            viewHolder.setUserUi(user)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var itemView: View = LayoutInflater.from(viewGroup!!.context).inflate(R.layout.user_data_layout, null)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView? = null
        var tvUserEmail: TextView? = null
        var user: User? = null

        init {
            tvUserName = itemView.findViewById(R.id.tv_user_name) as TextView
            tvUserEmail = itemView.findViewById(R.id.tv_user_email) as TextView
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (user != null && listener != null) {
                        listener!!.onItemClicked(user!!)
                    }
                }
            })
        }

        fun setUserUi(user: User) {
            this.user = user
            tvUserName!!.text = user.name
            tvUserEmail!!.text = user.email
        }
    }
}