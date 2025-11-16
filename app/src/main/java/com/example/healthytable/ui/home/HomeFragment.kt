package com.example.healthytable.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.healthytable.BuildConfig
import com.example.healthytable.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var recommendationTextView: TextView
    private val apiKey = BuildConfig.API_KEY // gemini에서 발급받은 apiKey

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recommendationTextView = view.findViewById(R.id.recommendationTextView)

        val sharedPrefs = requireActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val name = sharedPrefs.getString("name", "") ?: ""
        val age = sharedPrefs.getInt("age", 0)
        val gender = sharedPrefs.getString("gender", "") ?: ""
        val condition = sharedPrefs.getString("condition", "") ?: ""
        val height = sharedPrefs.getFloat("height", 0f)
        val weight = sharedPrefs.getFloat("weight", 0f)

        val prompt = createPrompt(name, age, gender, condition, height, weight)
        requestGeminiResponse(prompt) { result ->
            activity?.runOnUiThread {
                val cleaned = result
                    .replace(Regex("""\*\*(.*?)\*\*"""), "$1")
                    .replace(Regex("""#+\s*"""), "")
                recommendationTextView.text = cleaned

            }
        }
    }

    private fun createPrompt(name: String, age: Int, gender: String, condition: String, height: Float, weight: Float): String {
        return """
            당신은 전문 영양사입니다. 다음은 사용자 정보입니다:
            - 이름: $name
            - 나이: ${age}세
            - 성별: $gender
            - 건강 상태: $condition
            - 키: ${height}cm
            - 몸무게: ${weight}kg

            이 사용자에게 적절한 오늘의 식단을 아침, 점심, 저녁으로 나누어 추천해 주세요.
            각 식사에는 구체적인 음식명과 선택 이유(간략하게)도 포함해 주세요.
            출력 시 마크다운 문법(예: ##, **)을 사용하지 마세요.
        """.trimIndent()
    }

    private fun requestGeminiResponse(prompt: String, onResult: (String) -> Unit) {
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val jsonBody = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": "$prompt"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val client = OkHttpClient()
        val body = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GeminiAPI", "API 요청 실패: ${e.message}")
                onResult("식단 추천을 불러오지 못했습니다.")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    try {
                        val json = JSONObject(responseBody)
                        val text = json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        onResult(text)
                    } catch (e: Exception) {
                        Log.e("GeminiAPI", "파싱 오류: ${e.message}")
                        onResult("응답 처리 중 오류 발생")
                    }
                } else {
                    Log.e("GeminiAPI", "응답 실패: $responseBody")
                    onResult("식단 추천을 받지 못했습니다.")
                }
            }
        })
    }
}
