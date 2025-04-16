package com.dilz.labexam2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private var isRunImage = true  // Track which image is currently displayed
    private lateinit var scrollViewContent: LinearLayout
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        scrollViewContent = findViewById<ScrollView>(R.id.scrollView2).getChildAt(0) as LinearLayout
        searchEditText = findViewById(R.id.txttSearch)

        // Set up search functionality
        setupSearch()

        // Initial VM state toggle for demo VM (if needed)
        val initialImageView = findViewById<ImageView>(R.id.VM2Cntrl)
        initialImageView?.setOnClickListener {
            toggleVMState(initialImageView)
        }

        // Set up the Add VM floating action button
        val addVmButton = findViewById<FloatingActionButton>(R.id.btnAddVM)
        addVmButton.setOnClickListener {
            onClickAddVM(it)
        }

        // Set the username in the UI
        setUserInfo()

        // Load VMs from storage and display them
        loadAndDisplayVMs()
    }

    private fun setUserInfo() {
        // Get current user from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val currentUser = sharedPreferences.getString("current_user_email", "")

        // You could display this information in a user profile section if needed
    }

    private fun setupSearch() {
        // Add text change listener to filter VMs
        searchEditText.setOnEditorActionListener { _, _, _ ->
            filterVMs(searchEditText.text.toString().trim())
            true
        }
    }

    private fun filterVMs(query: String) {
        if (query.isEmpty()) {
            // If search query is empty, show all VMs
            loadAndDisplayVMs()
            return
        }

        // Get all VMs and filter them
        val sharedPreferences = getSharedPreferences("VMData", MODE_PRIVATE)
        val vmCount = sharedPreferences.getInt("vm_count", 0)

        // Clear current content before adding filtered results
        scrollViewContent.removeAllViews()

        // Keep the first fixed VM if present
        if (scrollViewContent.childCount > 0 && scrollViewContent.getChildAt(0) is ConstraintLayout) {
            val firstVM = scrollViewContent.getChildAt(0)
            scrollViewContent.removeAllViews()
            scrollViewContent.addView(firstVM)
        } else {
            scrollViewContent.removeAllViews()
        }

        // Add VMs that match the search query
        for (i in 1..vmCount) {
            val vmName = sharedPreferences.getString("vm_${i}_name", "") ?: ""
            val vmProvider = sharedPreferences.getString("vm_${i}_provider", "") ?: ""
            val vmOS = sharedPreferences.getString("vm_${i}_os", "") ?: ""

            // Check if VM matches search query
            if (vmName.contains(query, ignoreCase = true) ||
                vmProvider.contains(query, ignoreCase = true) ||
                vmOS.contains(query, ignoreCase = true)) {

                // Add VM to the list
                addVMToLayout(
                    i,
                    vmName,
                    vmProvider,
                    sharedPreferences.getString("vm_${i}_region", "") ?: "",
                    vmOS,
                    sharedPreferences.getString("vm_${i}_status", "Running") ?: "Running"
                )
            }
        }

        // Show message if no VMs match the search
        if (scrollViewContent.childCount == 0) {
            Snackbar.make(findViewById(R.id.main), "No VMs match your search", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadAndDisplayVMs() {
        // Clear existing content except the first fixed VM
        if (scrollViewContent.childCount > 0 && scrollViewContent.getChildAt(0) is ConstraintLayout) {
            val firstVM = scrollViewContent.getChildAt(0)
            scrollViewContent.removeAllViews()
            scrollViewContent.addView(firstVM)
        } else {
            scrollViewContent.removeAllViews()
        }

        // Get VM data from SharedPreferences
        val sharedPreferences = getSharedPreferences("VMData", MODE_PRIVATE)
        val vmCount = sharedPreferences.getInt("vm_count", 0)

        if (vmCount > 0) {
            // Add each VM to the layout
            for (i in 1..vmCount) {
                val vmName = sharedPreferences.getString("vm_${i}_name", null)
                if (vmName != null) {
                    addVMToLayout(
                        i,
                        vmName,
                        sharedPreferences.getString("vm_${i}_provider", "") ?: "",
                        sharedPreferences.getString("vm_${i}_region", "") ?: "",
                        sharedPreferences.getString("vm_${i}_os", "") ?: "",
                        sharedPreferences.getString("vm_${i}_status", "Running") ?: "Running"
                    )
                }
            }
        }
    }

    private fun addVMToLayout(id: Int, name: String, provider: String, region: String, os: String, status: String) {
        // Inflate VM item layout
        val vmLayout = layoutInflater.inflate(R.layout.vm_item, null) as ConstraintLayout

        // Set VM information
        val vmTitle = vmLayout.findViewById<TextView>(R.id.VM_Title)
        val vmLocation = vmLayout.findViewById<TextView>(R.id.VM_Location)
        val vmOsImage = vmLayout.findViewById<ImageView>(R.id.VM_Os)
        val vmControl = vmLayout.findViewById<ImageView>(R.id.VMCntrl)

        vmTitle.text = name
        vmLocation.text = "$provider - $region"

        // Set OS image based on the OS string
        val osImageResource = when {
            os.contains("Ubuntu", ignoreCase = true) -> R.drawable.os_ubuntu
            os.contains("Windows", ignoreCase = true) -> R.drawable.os_win
            os.contains("Debian", ignoreCase = true) -> R.drawable.os_debain
            os.contains("CentOS", ignoreCase = true) -> R.drawable.os_cent
            else -> R.drawable.os_ubuntu // Default
        }
        vmOsImage.setImageResource(osImageResource)

        // Set initial control button image based on status
        val controlImageResource = if (status == "Running") {
            R.drawable.run_btn
        } else {
            R.drawable.ran_controller
        }
        vmControl.setImageResource(controlImageResource)

        // Set up control button click listener
        vmControl.setOnClickListener {
            toggleVMState(vmControl)

            // Update status in SharedPreferences
            val sharedPreferences = getSharedPreferences("VMData", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val currentStatus = sharedPreferences.getString("vm_${id}_status", "Running")
            val newStatus = if (currentStatus == "Running") "Stopped" else "Running"
            editor.putString("vm_${id}_status", newStatus)
            editor.apply()

            // Show status change feedback
            Snackbar.make(
                findViewById(R.id.main),
                "VM '$name' is now $newStatus",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        // Set click listener to view VM details
        vmLayout.setOnClickListener {
            onClickVM(it)
        }

        // Add VM item to scrollview
        scrollViewContent.addView(vmLayout)

        // Add divider
        val divider = View(this)
        divider.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.divider_height)
        )
        divider.setBackgroundResource(R.drawable.seperator)
        scrollViewContent.addView(divider)
    }

    private fun toggleVMState(imageView: ImageView) {
        if (imageView.tag == "running" || imageView.tag == null) {
            imageView.setImageResource(R.drawable.ran_controller)
            imageView.tag = "stopped"
        } else {
            imageView.setImageResource(R.drawable.run_btn)
            imageView.tag = "running"
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh VM list when returning to this activity
        loadAndDisplayVMs()
    }

    fun onClickProfile(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun onClickVM(view: View) {
        val intent = Intent(this, VmViewActivity::class.java)
        startActivity(intent)
    }

    fun onClickShell(view: View) {
        val intent = Intent(this, ShellActivity::class.java)
        startActivity(intent)
    }

    fun onClickLogout(view: View) {
        // Clear login status in SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.apply()

        // Return to login screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun onClickAddVM(view: View) {
        val intent = Intent(this, AddVMActivity::class.java)
        startActivity(intent)
    }
}