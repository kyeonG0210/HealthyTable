package com.example.healthytable.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.healthytable.MainActivity
import com.example.healthytable.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class SettingsFragment : Fragment() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var ageEditText: TextInputEditText
    private lateinit var conditionEditText: TextInputEditText
    private lateinit var heightEditText: TextInputEditText
    private lateinit var weightEditText: TextInputEditText
    private lateinit var genderSpinner: AutoCompleteTextView //AutoCompleteTextView로 변경
    private lateinit var saveButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        ageEditText = view.findViewById(R.id.ageEditText)
        conditionEditText = view.findViewById(R.id.conditionEditText)
        heightEditText = view.findViewById(R.id.heightEditText)
        weightEditText = view.findViewById(R.id.weightEditText)
        genderSpinner = view.findViewById(R.id.genderSpinner)
        saveButton = view.findViewById(R.id.saveButton)

        val sharedPrefs = requireActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        // 성별 드롭다운 설정
        val genderOptions = listOf("여성", "남성", "선택 안함")
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        genderSpinner.setAdapter(genderAdapter)

        // 기존 저장값 불러오기
        nameEditText.setText(sharedPrefs.getString("name", ""))
        ageEditText.setText(sharedPrefs.getInt("age", 0).toString())
        conditionEditText.setText(sharedPrefs.getString("condition", ""))
        heightEditText.setText(sharedPrefs.getFloat("height", 0f).toString())
        weightEditText.setText(sharedPrefs.getFloat("weight", 0f).toString())
        genderSpinner.setText(sharedPrefs.getString("gender", ""), false)

        saveButton.setOnClickListener {
            val editor = sharedPrefs.edit()
            editor.putString("name", nameEditText.text.toString())
            editor.putInt("age", ageEditText.text.toString().toIntOrNull() ?: 0)
            editor.putString("condition", conditionEditText.text.toString())
            editor.putFloat("height", heightEditText.text.toString().toFloatOrNull() ?: 0f)
            editor.putFloat("weight", weightEditText.text.toString().toFloatOrNull() ?: 0f)
            editor.putString("gender", genderSpinner.text.toString())
            editor.apply()

            Toast.makeText(requireContext(), "정보가 저장되었습니다", Toast.LENGTH_SHORT).show()

            (requireActivity() as? MainActivity)?.updateUserNameInDrawer()

            val notification = NotificationCompat.Builder(requireContext(), "user_info_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("HealthyTable")
                .setContentText("${nameEditText.text}님의 정보가 저장되었습니다!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            NotificationManagerCompat.from(requireContext()).notify(1, notification)
        }

        // 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "user_info_channel",
                "사용자 설정 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = requireActivity().getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
