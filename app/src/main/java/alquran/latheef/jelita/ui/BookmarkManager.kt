package alquran.latheef.jelita.ui

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object BookmarkManager {
    private const val PREF_NAME = "QuranBookmarks"
    private const val KEY_BOOKMARKS = "bookmarks"
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun addBookmark(bookmark: Bookmark) {
        val bookmarks = getBookmarks().toMutableList()
        if (!bookmarks.contains(bookmark)) {
            bookmarks.add(bookmark)
            saveBookmarks(bookmarks)
        }
    }

    fun removeBookmark(bookmark: Bookmark) {
        val bookmarks = getBookmarks().toMutableList()
        bookmarks.remove(bookmark)
        saveBookmarks(bookmarks)
    }

    fun getBookmarks(): List<Bookmark> {
        val json = sharedPreferences.getString(KEY_BOOKMARKS, null) ?: return emptyList()
        val type = object : TypeToken<List<Bookmark>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    private fun saveBookmarks(bookmarks: List<Bookmark>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(bookmarks)
        editor.putString(KEY_BOOKMARKS, json)
        editor.apply()
    }
}