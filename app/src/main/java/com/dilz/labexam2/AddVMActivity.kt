package com.dilz.labexam2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AddVMActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vmactivity)

        // Set up card click listeners for better touch feedback
        setupCardClickListeners()
    }

    private fun setupCardClickListeners() {
        // AWS Card
        val cardAWS = findViewById<CardView>(R.id.cardAWS)
        cardAWS.setOnClickListener {
            navigateToCredentials("AWS")
        }

        // Azure Card
        val cardAzure = findViewById<CardView>(R.id.cardAzure)
        cardAzure.setOnClickListener {
            navigateToCredentials("Azure")
        }

        // Google Cloud Card
        val cardGCP = findViewById<CardView>(R.id.cardGCP)
        cardGCP.setOnClickListener {
            navigateToCredentials("Google Cloud")
        }

        // DigitalOcean Card
        val cardDigitalOcean = findViewById<CardView>(R.id.cardDigitalOcean)
        cardDigitalOcean.setOnClickListener {
            navigateToCredentials("DigitalOcean")
        }
    }

    // Handle Back button click
    fun onClickBack(view: View) {
        finish()
    }

    // Handle AWS provider selection
    fun onClickAWS(view: View) {
        navigateToCredentials("AWS")
    }

    // Handle Azure provider selection
    fun onClickAzure(view: View) {
        navigateToCredentials("Azure")
    }

    // Handle Google Cloud provider selection
    fun onClickGCP(view: View) {
        navigateToCredentials("Google Cloud")
    }

    // Handle DigitalOcean provider selection
    fun onClickDigitalOcean(view: View) {
        navigateToCredentials("DigitalOcean")
    }

    // Navigate to credentials page with provider information
    private fun navigateToCredentials(provider: String) {
        val intent = Intent(this, VMCredActivity::class.java)
        intent.putExtra("PROVIDER", provider)
        startActivity(intent)
    }
}