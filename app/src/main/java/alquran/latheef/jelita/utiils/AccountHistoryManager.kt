package alquran.latheef.jelita.utiils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser
import org.json.JSONArray
import org.json.JSONObject

object AccountHistoryManager {
    private const val PREF_NAME = "account_history"
    private const val KEY_ACCOUNTS = "accounts"

    fun saveAccount(context: Context, user: FirebaseUser) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_ACCOUNTS, "[]")
        val jsonArray = JSONArray(existing)

        // Hindari duplikat email
        if ((0 until jsonArray.length()).any { JSONObject(jsonArray.getString(it)).getString("email") == user.email }) return

        val obj = JSONObject().apply {
            put("name", user.displayName ?: "Unknown")
            put("email", user.email ?: "No Email")
            put("photoUrl", user.photoUrl?.toString() ?: "")
        }

        jsonArray.put(obj)
        prefs.edit().putString(KEY_ACCOUNTS, jsonArray.toString()).apply()
    }

    fun getAccountHistory(context: Context): List<Map<String, String>> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val list = mutableListOf<Map<String, String>>()
        val existing = prefs.getString(KEY_ACCOUNTS, "[]")
        val jsonArray = JSONArray(existing)

        for (i in 0 until jsonArray.length()) {
            val obj = JSONObject(jsonArray.getString(i))
            list.add(
                mapOf(
                    "name" to obj.getString("name"),
                    "email" to obj.getString("email"),
                    "photoUrl" to obj.getString("photoUrl")
                )
            )
        }
        return list
    }
}
