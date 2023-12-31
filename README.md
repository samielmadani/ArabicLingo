**ArabicLingo**
-


**Overview** -
ArabicLingo is a mobile app developed as part of the SENG440 course project. The app aims to assist Arabic users learn English and English users learn Arabic. The app has a list of Arabic phrases to be translated by the user to English, and the ability of extracting text from images, translating it into Arabic (built in English to Arabic translator), and facilitating sharing or saving of the translated text.

**Purpose** -
The motivation behind ArabicLingo was to create a practical tool for individuals who frequently encounter written content within images and seek to comprehend the text in Arabic. This utility could be valuable for travelers, language learners, or anyone grappling with text in photos.


**Development Process** - 
The app's user interface was designed using Kotlin and XML, focusing on an intuitive and fluid user experience. The app encompasses various screens, such as camera, list views, text extraction, translation, and notifications, all organized through fragments.

**Grade-Bearing Requirements Met**

1. **Multiple Screens:** The app implements distinct screens, including the camera, translate learning page, modals, text extraction, app instructions, translation, and notifications, utilizing fragments, using a navigation bar.

2. **Implicit Intent:** Implicit intents of the camera app empower users to effortlessly share the translated text with other applications.

3. **List View (RecyclerView):** ArabicLingo utilizes RecyclerView to present the list of arabic phrases the user will translate to learn, enabling seamless scrolling.

4. **Multiple Widgets/UI Components:** The app integrates buttons, image buttons, checkboxes, text boxes, text views, and more, equipped with event listeners to manage interactions.

5. **Multiple Layouts:** ConstraintLayout and LinearLayout foster diverse layouts, contributing to a consistent user experience.

6. **Support for Landscape and Portrait Orientations:** All screens adapt gracefully to both landscape and portrait orientations.

7. **Separate Layouts for Landscape and Portrait:** The app provides distinct layout resources for at least one view, ensuring optimal display in both orientations.

8. **String Resources:** All static text within the user interface is managed as string resources, easing localization and updates.

9. **Localization:** ArabicLingo is localized for English and Arabic, accommodating string resources for both languages.

10. **Toast Message:** Toast messages provide real-time user feedback and alerts.

11. **Animation:** XML-defined animations enhance the user interface's visual appeal, on the instructions tab.

12. **Additional Android API Feature:** ArabicLingo employs the Google Text Extraction and ML Kit for text recognition and translation.

13. **Additional Android API Feature:** The app harnesses Android's notification system to promptly inform users of translated text.

