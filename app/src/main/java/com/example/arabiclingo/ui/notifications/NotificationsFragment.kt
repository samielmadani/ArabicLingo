package com.example.arabiclingo.ui.notifications

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.arabiclingo.databinding.FragmentNotificationsBinding
import java.io.File
import java.io.IOException


class NotificationsFragment : Fragment() {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val CAMERA = 101
        private lateinit var photoFile: File
        private const val FILE_NAME = "photo.jpg"
    }

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val takePictureButton: Button = binding.btnTakePicture
        takePictureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted, open the camera
                openCamera()
            } else {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }

        return root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                openCamera()
            } else {
                // Permission denied, handle accordingly
                // You can show a message or take other actions here
            }
        }
    }

    private fun openCamera() {
        photoFile = getPhotoFile(FILE_NAME)
        val fileProvider = FileProvider.getUriForFile(
            requireContext(),
            "com.example.arabiclingo.fileprovider",
            photoFile
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        startActivityForResult(cameraIntent, CAMERA)
    }


    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun rotateImageIfRequired(image: Bitmap, imagePath: String): Bitmap {
        val exif = try {
            ExifInterface(imagePath)
        } catch (e: IOException) {
            return image
        }

        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = android.graphics.Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270F)
        }

        return Bitmap.createBitmap(
            image, 0, 0, image.width, image.height, matrix, true
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == AppCompatActivity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            val rotatedImage = rotateImageIfRequired(takenImage, photoFile.absolutePath)
            binding.imageView.setImageBitmap(rotatedImage)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}