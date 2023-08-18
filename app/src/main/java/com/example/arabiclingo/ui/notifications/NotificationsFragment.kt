package com.example.arabiclingo.ui.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.arabiclingo.R
import com.example.arabiclingo.databinding.FragmentNotificationsBinding
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class NotificationsFragment : Fragment() {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val CAMERA = 101
        private lateinit var photoFile: File
        private const val FILE_NAME = "photo.jpg"
        private const val CHANNEL_ID = "my_channel_id"
        private var savedImageBitmap: Bitmap? = null

    }

    private var _binding: FragmentNotificationsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createNotificationChannel()
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
                openCamera()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }



        val shareButton: ImageButton = binding.btnShare
        shareButton.setOnClickListener {
            shareImage()
        }

        val saveButton: ImageButton = binding.btnSave
        saveButton.setOnClickListener {
            saveImageToGallery()
        }

        if (savedImageBitmap != null) {
            binding.imageView.setImageBitmap(savedImageBitmap)
            binding.btnSave.isEnabled = true
            binding.btnSave.setBackgroundResource(R.drawable.circle_button_background)
            binding.btnShare.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
        }

        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.btnSave.startAnimation(fadeInAnimation)
        binding.btnShare.startAnimation(fadeInAnimation)
        binding.imageView.startAnimation(fadeInAnimation)
        binding.btnTakePicture.startAnimation(fadeInAnimation)


        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        savedImageBitmap = (binding.imageView.drawable as? BitmapDrawable)?.bitmap
    }


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

        val imgSave = getString(R.string.image_saved)


        Toast.makeText(requireContext(), imgSave, Toast.LENGTH_SHORT).show()
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
                openCamera()
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

            binding.btnSave.isEnabled = true
            binding.btnSave.setBackgroundResource(R.drawable.circle_button_background)
            binding.btnShare.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE

            performTextRecognition(rotatedImage)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedImageBitmap?.let {
            binding.imageView.setImageBitmap(it)
            binding.btnSave.isEnabled = true
            binding.btnSave.setBackgroundResource(R.drawable.circle_button_background)
            binding.btnShare.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
        }
    }


    private fun performTextRecognition(image: Bitmap) {
        val textRecognizer = TextRecognizer.Builder(requireContext()).build()
        if (!textRecognizer.isOperational) {
            return
        }

        val frame = Frame.Builder().setBitmap(image).build()
        val textBlocks = textRecognizer.detect(frame)

        val extractedText = StringBuilder()
        for (i in 0 until textBlocks.size()) {
            val textBlock = textBlocks.valueAt(i)
            extractedText.append(textBlock.value)
            extractedText.append("\n")
        }

        if (extractedText.toString().isEmpty()) {
            showTextNotification(extractedText.toString())
        } else {
            translateIfNecessary(extractedText.toString())
        }


    }

    private fun translateIfNecessary(text: String) {
        val eng = getString(R.string.english_translation)
        val ara = getString(R.string.arabic_translation)
        val translFail = getString(R.string.translation_failed)
        val modelFail = getString(R.string.model_download_failed)

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.ARABIC)
            .build()

        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        showTextNotification("$eng \n\n$text \n\n $ara \n\n$translatedText")
                    }
                    .addOnFailureListener { exception ->
                        showToast("$translFail ${exception.message}")
                    }
                    .addOnCompleteListener {
                        translator.close()
                    }
            }
            .addOnFailureListener { exception ->
                showToast("$modelFail ${exception.message}")
            }
    }


    @SuppressLint("MissingPermission")
    private fun showTextNotification(text: String) {
        val missing = getString(R.string.no_text_extracted)
        val extract = getString(R.string.extracted_text)
        val notifs = getString(R.string.notifications_not_enabled)

        val text = text.ifEmpty { "\uD83D\uDE2D $missing \uD83D\uDE2D" }

        if (NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            val notificationId = 123

            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setColor(ContextCompat.getColor(requireContext(), R.color.notification_icon_background_color))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(extract)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(requireContext())
            notificationManager.notify(notificationId, builder.build())
        } else {

            showToast(notifs)
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "My notification channel"
            val notificationManager = NotificationManagerCompat.from(requireContext())
            notificationManager.createNotificationChannel(channel)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnShare.visibility = View.GONE
        binding.btnSave.visibility = View.GONE
        binding.btnTakePicture.visibility = View.VISIBLE
        _binding = null

    }
}
