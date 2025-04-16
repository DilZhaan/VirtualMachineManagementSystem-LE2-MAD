package com.dilz.labexam2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    private var isRunImage = true  // Track which image is currently displayed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageView = findViewById<ImageView>(R.id.VM2Cntrl)
        imageView.setOnClickListener {
            if (isRunImage) {
                imageView.setImageResource(R.drawable.ran_controller)
            } else {
                imageView.setImageResource(R.drawable.run_btn)
            }
            isRunImage = !isRunImage  // Toggle the state
        }
    }
    fun onClickProfile(vi: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun onClickVM(vi: View) {
        val intent = Intent(this, VmViewActivity::class.java)
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

}