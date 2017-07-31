package com.lalit.firebaseexamplekotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lalit.firebaseexamplekotlin.adapter.UserDataRecyclerAdapter
import com.lalit.firebaseexamplekotlin.model.User

class MainActivity : AppCompatActivity(), View.OnClickListener, UserDataRecyclerAdapter.OnItemClickListener, ValueEventListener {

    var firebaseDatabase: FirebaseDatabase? = null
    var dbReference: DatabaseReference? = null
    var recyclerView: RecyclerView? = null
    var etUserName: TextInputEditText? = null
    var etUserEmail: TextInputEditText? = null
    private var selectedUser: User? = null
    var btnAddUser: Button? = null
    var btnUpdateUser: Button? = null
    var btnDeleteUser: Button? = null
    private var userList: ArrayList<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        etUserEmail = findViewById(R.id.tiet_user_email) as TextInputEditText
        etUserName = findViewById(R.id.tiet_user_name) as TextInputEditText
        btnAddUser = findViewById(R.id.btn_add_user) as Button
        btnUpdateUser = findViewById(R.id.btn_update_user) as Button
        btnDeleteUser = findViewById(R.id.btn_delete_user) as Button
        btnAddUser?.setOnClickListener(this)
        btnUpdateUser?.setOnClickListener(this)
        btnDeleteUser?.setOnClickListener(this)
        val path = "app_user/${FirebaseAuth.getInstance().currentUser!!.uid}"
        firebaseDatabase = FirebaseDatabase.getInstance()
        Log.i("title", "path: $path")
        dbReference = firebaseDatabase!!.getReference("$path/user")
        dbReference?.addValueEventListener(this)
        FirebaseApp.initializeApp(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnAddUser -> {
                if (etUserEmail!!.text.isNotBlank() && etUserName!!.text.isNotBlank()) {
                    val id = dbReference!!.push().key
                    val user: User = User(etUserName!!.text.toString(), etUserEmail!!.text.toString(), id)
                    dbReference!!.child(user!!.id).setValue(user)
                }
            }
            btnDeleteUser -> {
                if (selectedUser != null) {
                    dbReference!!.child(selectedUser!!.id).removeValue()
                }
            }

            btnUpdateUser -> {
                if (selectedUser != null) {
                    selectedUser!!.name = etUserName!!.text.toString()
                    selectedUser!!.email = etUserEmail!!.text.toString()
                    dbReference!!.child(selectedUser!!.id).updateChildren(User.getKeyMap(selectedUser!!))
                }
            }
        }
        etUserEmail!!.text.clear()
        etUserName!!.text.clear()
        selectedUser = null
    }

    override fun onItemClicked(user: User) {
        selectedUser = user
        etUserName!!.setText(user.name)
        etUserEmail!!.setText(user.email)
    }

    override fun onCancelled(p0: DatabaseError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataChange(dataSnapshot: DataSnapshot?) {
        if (userList != null && userList!!.isEmpty()) {
            userList!!.clear()
        } else {
            userList = ArrayList<User>()
        }

        if (dataSnapshot != null) {
            for (dbSnap in dataSnapshot.children) {
                val user = dbSnap.getValue(User::class.java)
                if (user is User && user.id == null) {
                    user.id = dbSnap.key
                }
                userList!!.add(user)
            }
        }
        val adapter = UserDataRecyclerAdapter(userList!!, this)
        recyclerView!!.adapter = adapter
    }

}
