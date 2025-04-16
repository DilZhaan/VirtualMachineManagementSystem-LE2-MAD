package com.dilz.labexam2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun onClickProfile(vi: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun onClickShell(vi: View) {
        val intent = Intent(this, ShellActivity::class.java)
        startActivity(intent)
    }

    fun onClickLogout(vi: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun onClickHome(vi: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}