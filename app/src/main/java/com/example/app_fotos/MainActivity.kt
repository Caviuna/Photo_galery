package com.example.app_fotos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var image_uri: Uri? = null

    companion object {
        private val PERMISSION_CODE_IMAGE_PICK = 1000
        private val IMAGE_PICK_CODE = 1001

        private val PERMISSION_CODE_CAMERA_CAPTURE = 2000
        private val OPEN_CAMERA_CODE = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pick_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //obsolete version, but didactic analysis

                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){

                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                    requestPermissions(permission, PERMISSION_CODE_IMAGE_PICK)
                }
                else{
                    pickImageFromGalery()
                }
            }
            else{
                pickImageFromGalery()

            }
        }

        open_camera_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {

                    val permission = arrayOf(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE )

                    requestPermissions(permission, PERMISSION_CODE_CAMERA_CAPTURE)

                }
                else{
                    openCamera()
                }
            }
            else{
                openCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // add after
        when(requestCode){
            PERMISSION_CODE_IMAGE_PICK -> {
                // is not Empty add after, before is .size
                if (grantResults.isNotEmpty()
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGalery()
                }
                else{
                    Toast.makeText(this, "Permissão Negada", Toast.LENGTH_SHORT).show()
                }
            }

            PERMISSION_CODE_CAMERA_CAPTURE -> {
                if (grantResults.size > 1
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                }
                else{
                    Toast.makeText(this, "Permissão Negada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun pickImageFromGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE) // check deprecation
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem capturada pela camera")

        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)

        startActivityForResult(cameraIntent, OPEN_CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_view.setImageURI(data?.data)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_CAMERA_CODE) {
            image_view.setImageURI(image_uri)
        }
    }
}