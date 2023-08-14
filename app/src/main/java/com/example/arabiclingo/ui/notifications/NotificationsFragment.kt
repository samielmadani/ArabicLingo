package com.example.arabiclingo.ui.notifications

import android.Manifest
import android.content.ContentValues
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
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.arabiclingo.R
import com.example.arabiclingo.databinding.FragmentNotificationsBinding
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import java.io.File
import java.io.FileInputStream
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


        // Inside your onCreateView function

        val shareButton: ImageButton = binding.btnShare
        shareButton.setOnClickListener {
            shareImage()
        }

        val saveButton: ImageButton = binding.btnSave
        saveButton.setOnClickListener {
            saveImageToGallery()
        }


        return root
    }

    // Inside your NotificationsFragment class

    private fun shareImage() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.arabiclingo.fileprovider",
            photoFile
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    private fun saveImageToGallery() {
        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.arabiclingo.fileprovider",
            photoFile
        )

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "ArabicLingo-image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/ArabicLingo")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = requireContext().contentResolver
        val imageCollectionUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val imageUriResult = resolver.insert(imageCollectionUri, values)

        resolver.openOutputStream(imageUriResult!!)?.use { outputStream ->
            val inputStream = FileInputStream(photoFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(imageUriResult, values, null, null)


        Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show()
        binding.btnSave.isEnabled = false
        binding.btnSave.setBackgroundResource(R.drawable.circle_button_disabled)
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

            // Enable the buttons after an image is captured
            binding.btnSave.isEnabled = true
            binding.btnSave.setBackgroundResource(R.drawable.circle_button_background)
            binding.btnShare.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE

            // Perform text recognition on the captured image
            performTextRecognition(rotatedImage)
        }
    }

    private fun performTextRecognition(image: Bitmap) {
        val textRecognizer = TextRecognizer.Builder(requireContext()).build()
        if (!textRecognizer.isOperational) {
            // Text recognizer is not available, handle this case
            return
        }

        val frame = Frame.Builder().setBitmap(image).build()
        val textBlocks = textRecognizer.detect(frame)

        // Process textBlocks and extract the recognized text
        val extractedText = StringBuilder()
        for (i in 0 until textBlocks.size()) {
            val textBlock = textBlocks.valueAt(i)
            extractedText.append(textBlock.value)
            extractedText.append("\n")
        }

        // Now you can use the extracted text as needed
        showToast("Try to translate:\n $extractedText")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Disable the buttons when the view is destroyed
        binding.btnShare.visibility = View.GONE
        binding.btnSave.visibility = View.GONE
        binding.btnTakePicture.visibility = View.VISIBLE
        _binding = null

    }
}