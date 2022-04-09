package com.lab3.lab3

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.lab3.lab3.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var tempImageUri : Uri? = null
    private val group : String = "УІ-191"
    private val lastName : String = "Кузнєцов"
    private val emailReceiver : List<String> = listOf("hodovychenko.labs@gmail.com")


    // permission to camera
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if(it){
            tempImageUri = getTempUri()
            resultLauncher.launch(tempImageUri)
        }else{
            Toast.makeText(this,"Can not open camera without permission :(",Toast.LENGTH_SHORT).show()
        }
    }

    //set result photo to imageView
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){
        success ->
        if(success){
            binding.selfie.setImageURI(tempImageUri)
        }else{}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ClickListener on button "Take a shot"
        binding.buttonToShot.setOnClickListener{
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        //ClickListener on button "Send a selfie"
        binding.buttonToSend.setOnClickListener {
            if(tempImageUri == null){
                Toast.makeText(this,"Take a shot before it send!",Toast.LENGTH_SHORT).show()
            }else{
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "application/octet-stream"
                sendIntent.putExtra(Intent.EXTRA_EMAIL , emailReceiver.toTypedArray())
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,"КПП $group $lastName")
                sendIntent.putExtra(Intent.EXTRA_STREAM,tempImageUri)

                startActivity(sendIntent)
            }
        }
    }

    //get uri of taken photo
    private fun getTempUri(): Uri {
        val tempFile = File.createTempFile("tmp_image_file", ".jpg", getExternalFilesDir("my_images")).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tempFile)
    }

}