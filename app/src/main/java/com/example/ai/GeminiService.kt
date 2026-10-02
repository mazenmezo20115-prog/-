package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    suspend fun generateTeamIdentity(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackTeamIdentity(prompt)
        }

        val systemInstruction = "أنت خبير ومدير فني في كرة القدم الخماسية. " +
                "اقترح أسماء فرق عربية حماسية ومميزة لمباريات الخماسي، وشعار الفريق، ونصيحة تكتيكية قصيرة. " +
                "اجعل الرد ملخصاً ومشجعاً باللغة العربية بالكامل وبنقاط واضحة."

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "طلب المستخدم: $prompt\n\n$systemInstruction"))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL/$MODEL:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext getFallbackTeamIdentity(prompt)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text
            } else {
                getFallbackTeamIdentity(prompt)
            }
        } catch (e: Exception) {
            getFallbackTeamIdentity(prompt)
        }
    }

    suspend fun generateTacticalAdvice(formation: String, opponentStyle: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackTactics(formation, opponentStyle)
        }

        try {
            val prompt = "قدم خطة تكتيكية سريعة ومختصرة لمباراة كرة قدم خماسية بتشكيلة $formation ضد منافس يلعب بأسلوب '$opponentStyle'. " +
                    "ركز على: سرعة الارتداد، والتسديد من المسافات، وتدوير الكرة في المساحات الضيقة، باللغة العربية."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL/$MODEL:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) return@withContext getFallbackTactics(formation, opponentStyle)

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) text else getFallbackTactics(formation, opponentStyle)
        } catch (e: Exception) {
            getFallbackTactics(formation, opponentStyle)
        }
    }

    private fun getFallbackTeamIdentity(prompt: String): String {
        val clean = prompt.trim()
        val suffix = if (clean.isNotBlank()) clean else "الصقور"
        return """
            ⚽ **هوية واقتراحات فريق الخماسي: '$suffix'**
            
            • **الأسماء المقترحة:**
              1. صقور $suffix الخماسي
              2. نجوم $suffix الذهبي
              3. أبطال التحدي الخماسي
              4. كتيبة $suffix السريعة
            
            • **شعار الفريق:** "كرة سريعة، روح واحدة، وفوز مستحق"
            • **فلسفة اللعب الخماسي:** التحول السريع، الاعتماد على التمريرات السريعة (One-Two) والتسديد المباشر على المرمى.
        """.trimIndent()
    }

    private fun getFallbackTactics(formation: String, opponentStyle: String): String {
        return """
            📋 **التوجيه التكتيكي لمباراة الخماسي ($formation ضد $opponentStyle)**
            
            1. **في حالة الاستحواذ:**
               - تدوير سريع للكرة بلمستين كحد أقصى لتفكيك التكتل الدفاعي.
               - فتح مساحات بالتحرك بدون كرة وخلق زاوية تسديد نظيفة.
            
            2. **في التحول الدفاعي:**
               - تراجع ثنائي الدفاع فوراً لحماية المرمى وإغلاق زوايا التمرير.
               - الحارس يلعب كليبرو متقدم لتشتيت الكرات الطويلة.
            
            3. **نقطة الحسم:**
               - الضغط العالي في منتصف الملعب عند استلام المنافس بظهره للملعب.
        """.trimIndent()
    }
}
