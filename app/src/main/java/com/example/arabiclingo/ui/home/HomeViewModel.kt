package com.example.arabiclingo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to ArabicLingo! \n\n" +
                "Learn Arabic effortlessly with ArabicLingo. Explore the Learn Tab to practice translations and perfect your skills. " +
                "In the Camera Tab, capture handwritten Arabic notes and share them. Your journey to Arabic fluency starts here."
    }
    val text: LiveData<String> = _text

    private val _instructions = MutableLiveData<String>().apply {
        value = "\nLearn Tab:\n" +
                "\n" +
                "In the Learn tab, you will find a list of Arabic phrases. To start learning, follow these steps:\n" +
                "\n" +
                "1. Tap on a specific phrase from the list.\n" +
                "2. A box will appear where you can enter the English translation of the selected Arabic phrase.\n" +
                "3. Press the \"Submit\" button.\n" +
                "4. If your translation is correct, the phrase will appear in green.\n" +
                "5. If your translation is incorrect, the phrase will appear in red.\n" +
                "\n" +
                "With the Learn tab, you can practice and enhance your Arabic vocabulary by translating phrases accurately.\n" +
                "\n\n\n" +
                "Camera Tab:\n" +
                "\n" +
                "In the Camera tab, you can utilize your phone's camera for a unique learning experience:\n" +
                "\n" +
                "1. Tap on the Camera tab.\n" +
                "2. Use your phone's camera to take a photo of your handwritten Arabic writing.\n" +
                "3. You can save the photo to your camera roll for future reference.\n" +
                "4. You can also share the photo with your friends to get feedback or discuss your progress.\n" +
                "\n" +
                "The Camera tab provides an innovative way to practice your Arabic writing skills and share your learning journey with others.\n" +
                "\n" +
                "We hope you enjoy using ArabicLingo to learn Arabic and enhance your language skills. Happy learning!\n"
    }
    val instructions: LiveData<String> = _instructions
}