package com.singularity.ipcaplus

import android.content.Intent
import android.graphics.drawable.Icon
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.singularity.ipcaplus.databinding.ActivityAddPeopleBinding
import com.singularity.ipcaplus.databinding.ActivitySearchBinding
import com.singularity.ipcaplus.models.Chat
import com.singularity.ipcaplus.models.Profile
import com.singularity.ipcaplus.utils.Backend
import com.singularity.ipcaplus.utils.Utilis

class AddPeopleActivity: AppCompatActivity() {

    var users = arrayListOf<Profile>()
    var selectedUsers = arrayListOf<String>()

    private lateinit var binding: ActivityAddPeopleBinding

    private var userAdapter: RecyclerView.Adapter<*>? = null
    private var userSelectedAdapter: RecyclerView.Adapter<*>? = null
    private var userLayoutManager: LinearLayoutManager? = null


    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_people)
        binding = ActivityAddPeopleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val back = findViewById<ImageView>(R.id.back_btn)

        back.setOnClickListener(){
            finish()
        }

        // Get Users
        Backend.getAllUsers {
            users.addAll(it)
        }


        // Recycler View All Users
        userLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewUsers.layoutManager = userLayoutManager
        userAdapter = AllUsersAdapter()
        binding.recyclerViewUsers.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewUsers.adapter = userAdapter


    }


    inner class AllUsersAdapter : RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {

        inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.v.apply {
                // Variables
                val username = findViewById<TextView>(R.id.textViewProfileName)
                val imageViewUser = findViewById<ImageView>(R.id.imageViewProfileGroup)

                // Set data
                Utilis.getFile("profilePictures/${users[position].id}.png", "png") { bitmap ->
                    imageViewUser.setImageBitmap(bitmap)
                }

                username.text = users[position].name

            }
            holder.v.setOnClickListener {

            }

        }

        override fun getItemCount(): Int {
            return users.size
        }
    }
}