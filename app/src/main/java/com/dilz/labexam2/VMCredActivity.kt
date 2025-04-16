package com.dilz.labexam2

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VMCredActivity : AppCompatActivity() {

    private lateinit var providerName: String
    private lateinit var tvProviderName: TextView
    private lateinit var tilAccessKey: TextInputLayout
    private lateinit var tilSecretKey: TextInputLayout
    private lateinit var tilVMName: TextInputLayout
    private lateinit var tilRegion: TextInputLayout
    private lateinit var tilOS: TextInputLayout
    private lateinit var tilInstanceType: TextInputLayout
    private lateinit var etAccessKey: TextInputEditText
    private lateinit var etSecretKey: TextInputEditText
    private lateinit var etVMName: TextInputEditText
    private lateinit var dropdownRegion: AutoCompleteTextView
    private lateinit var dropdownOS: AutoCompleteTextView
    private lateinit var dropdownInstanceType: AutoCompleteTextView
    private lateinit var btnCreateVM: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vmcred)

        // Get provider name from intent
        providerName = intent.getStringExtra("PROVIDER") ?: "Cloud Provider"

        // Initialize UI components
        initializeViews()

        // Set provider name in the UI
        tvProviderName.text = providerName

        // Set up dropdown menus
        setupDropdowns()

        // Set up create button listener
        btnCreateVM.setOnClickListener {
            if (validateInputs()) {
                createVirtualMachine()
            }
        }
    }

    private fun initializeViews() {
        tvProviderName = findViewById(R.id.tvProviderName)

        // TextInputLayouts for validation
        tilAccessKey = findViewById(R.id.textInputLayoutAccessKey)
        tilSecretKey = findViewById(R.id.textInputLayoutSecretKey)
        tilVMName = findViewById(R.id.textInputLayoutVMName)
        tilRegion = findViewById(R.id.textInputLayoutRegion)
        tilOS = findViewById(R.id.textInputLayoutOS)
        tilInstanceType = findViewById(R.id.textInputLayoutInstanceType)

        // EditTexts and Dropdowns
        etAccessKey = findViewById(R.id.etAccessKey)
        etSecretKey = findViewById(R.id.etSecretKey)
        etVMName = findViewById(R.id.etVMName)
        dropdownRegion = findViewById(R.id.dropdownRegion)
        dropdownOS = findViewById(R.id.dropdownOS)
        dropdownInstanceType = findViewById(R.id.dropdownInstanceType)

        // Buttons
        btnCreateVM = findViewById(R.id.btnCreateVM)
    }

    // Set up dropdown menus with appropriate options based on provider
    private fun setupDropdowns() {
        // Example regions (would be different for each provider)
        val regions = when (providerName) {
            "AWS" -> arrayOf("US East (N. Virginia)", "US West (Oregon)", "Europe (Ireland)", "Asia Pacific (Tokyo)")
            "Azure" -> arrayOf("East US", "West Europe", "Southeast Asia", "Brazil South")
            "Google Cloud" -> arrayOf("us-central1", "europe-west1", "asia-east1", "australia-southeast1")
            "DigitalOcean" -> arrayOf("NYC1", "LON1", "AMS3", "SGP1")
            else -> arrayOf("Region 1", "Region 2", "Region 3")
        }

        // OS options
        val operatingSystems = arrayOf("Ubuntu 20.04", "Debian 11", "CentOS 8", "Windows Server 2019")

        // Instance types (simplified example)
        val instanceTypes = when (providerName) {
            "AWS" -> arrayOf("t2.micro", "t2.small", "m5.large", "c5.xlarge")
            "Azure" -> arrayOf("B1s", "B2s", "D2s v3", "F2s v2")
            "Google Cloud" -> arrayOf("e2-micro", "e2-small", "n1-standard-1", "n1-standard-2")
            "DigitalOcean" -> arrayOf("s-1vcpu-1gb", "s-1vcpu-2gb", "s-2vcpu-2gb", "s-2vcpu-4gb")
            else -> arrayOf("Small", "Medium", "Large")
        }

        // Set up region dropdown
        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regions)
        dropdownRegion.setAdapter(regionAdapter)

        // Set default value for better UX
        if (regions.isNotEmpty()) {
            dropdownRegion.setText(regions[0], false)
        }

        // Set up OS dropdown
        val osAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, operatingSystems)
        dropdownOS.setAdapter(osAdapter)

        // Set default OS to Ubuntu
        dropdownOS.setText("Ubuntu 20.04", false)

        // Set up instance type dropdown
        val instanceAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, instanceTypes)
        dropdownInstanceType.setAdapter(instanceAdapter)

        // Set default instance type to the first one
        if (instanceTypes.isNotEmpty()) {
            dropdownInstanceType.setText(instanceTypes[0], false)
        }
    }

    // Validate user inputs with error display
    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous errors
        tilAccessKey.error = null
        tilSecretKey.error = null
        tilVMName.error = null
        tilRegion.error = null
        tilOS.error = null
        tilInstanceType.error = null

        // Get input values
        val accessKey = etAccessKey.text.toString().trim()
        val secretKey = etSecretKey.text.toString().trim()
        val vmName = etVMName.text.toString().trim()
        val region = dropdownRegion.text.toString().trim()
        val os = dropdownOS.text.toString().trim()
        val instanceType = dropdownInstanceType.text.toString().trim()

        // Validate API key
        if (accessKey.isEmpty()) {
            tilAccessKey.error = "API Key/Access Key is required"
            isValid = false
        } else if (accessKey.length < 8) {
            tilAccessKey.error = "API Key must be at least 8 characters"
            isValid = false
        }

        // Validate Secret Key
        if (secretKey.isEmpty()) {
            tilSecretKey.error = "Secret Key is required"
            isValid = false
        } else if (secretKey.length < 8) {
            tilSecretKey.error = "Secret Key must be at least 8 characters"
            isValid = false
        }

        // Validate VM Name
        if (vmName.isEmpty()) {
            tilVMName.error = "VM Name is required"
            isValid = false
        } else if (vmName.length < 3) {
            tilVMName.error = "VM Name must be at least 3 characters"
            isValid = false
        } else if (!isVmNameValid(vmName)) {
            tilVMName.error = "VM Name can only contain letters, numbers, and hyphens"
            isValid = false
        } else if (isVmNameTaken(vmName)) {
            tilVMName.error = "VM Name is already in use"
            isValid = false
        }

        // Validate Region
        if (region.isEmpty()) {
            tilRegion.error = "Please select a Region"
            isValid = false
        }

        // Validate OS
        if (os.isEmpty()) {
            tilOS.error = "Please select an Operating System"
            isValid = false
        }

        // Validate Instance Type
        if (instanceType.isEmpty()) {
            tilInstanceType.error = "Please select an Instance Type"
            isValid = false
        }

        return isValid
    }

    // Check if VM name only contains valid characters
    private fun isVmNameValid(name: String): Boolean {
        return name.matches(Regex("^[a-zA-Z0-9\\-]+$"))
    }

    // Check if VM name is already taken
    private fun isVmNameTaken(name: String): Boolean {
        val sharedPreferences = getSharedPreferences("VMData", MODE_PRIVATE)
        val vmCount = sharedPreferences.getInt("vm_count", 0)

        for (i in 1..vmCount) {
            val existingName = sharedPreferences.getString("vm_${i}_name", "")
            if (existingName.equals(name, ignoreCase = true)) {
                return true
            }
        }

        return false
    }

    // Create the virtual machine
    private fun createVirtualMachine() {
        // Get all input values
        val accessKey = etAccessKey.text.toString().trim()
        val secretKey = etSecretKey.text.toString().trim()
        val vmName = etVMName.text.toString().trim()
        val region = dropdownRegion.text.toString().trim()
        val os = dropdownOS.text.toString().trim()
        val instanceType = dropdownInstanceType.text.toString().trim()

        try {
            // Save VM data to SharedPreferences
            saveVMToSharedPreferences(vmName, providerName, region, os, instanceType)

            // Save VM data to JSON file (internal storage)
            saveVMToInternalStorage(vmName, providerName, region, os, instanceType, accessKey, secretKey)

            // Show success message
            Snackbar.make(
                findViewById(android.R.id.content),
                "Virtual Machine '$vmName' is being created",
                Snackbar.LENGTH_LONG
            ).show()

            // Return to the previous screen after a short delay
            findViewById<View>(android.R.id.content).postDelayed({
                finish()
            }, 1500)

        } catch (e: Exception) {
            // Handle errors
            Snackbar.make(
                findViewById(android.R.id.content),
                "Error creating VM: ${e.message}",
                Snackbar.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    // Save VM information to SharedPreferences
    private fun saveVMToSharedPreferences(name: String, provider: String, region: String, os: String, instanceType: String) {
        val sharedPreferences = getSharedPreferences("VMData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Get current VM count for unique ID
        val vmCount = sharedPreferences.getInt("vm_count", 0)
        val newVmId = vmCount + 1

        // Store VM data with unique ID
        editor.putString("vm_${newVmId}_name", name)
        editor.putString("vm_${newVmId}_provider", provider)
        editor.putString("vm_${newVmId}_region", region)
        editor.putString("vm_${newVmId}_os", os)
        editor.putString("vm_${newVmId}_instance", instanceType)
        editor.putString("vm_${newVmId}_status", "Running")
        editor.putString("vm_${newVmId}_created_date", System.currentTimeMillis().toString())

        // Update VM count
        editor.putInt("vm_count", newVmId)

        // Apply changes
        editor.apply()
    }

    // Save VM information to internal storage (JSON)
    private fun saveVMToInternalStorage(
        name: String,
        provider: String,
        region: String,
        os: String,
        instanceType: String,
        accessKey: String,
        secretKey: String
    ) {
        val vmFile = File(filesDir, "vms.json")

        // Create VM JSON object
        val vmObject = JSONObject().apply {
            put("name", name)
            put("provider", provider)
            put("region", region)
            put("os", os)
            put("instanceType", instanceType)
            put("status", "Running")
            put("createdAt", System.currentTimeMillis())
            // Encrypt or hash these in a real app before storing
            put("accessKey", accessKey)
            put("secretKey", secretKey)
        }

        try {
            val jsonArray = if (vmFile.exists()) {
                val jsonString = vmFile.readText()
                JSONArray(jsonString)
            } else {
                JSONArray()
            }

            jsonArray.put(vmObject)

            FileWriter(vmFile).use { writer ->
                writer.write(jsonArray.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to save VM data: ${e.message}")
        }
    }

    // Handle Back button click
    fun onClickBack(view: View) {
        finish()
    }

    // Handle Create VM button click (now handled by the button listener in onCreate)
    fun onClickCreateVM(view: View) {
        if (validateInputs()) {
            createVirtualMachine()
        }
    }
}