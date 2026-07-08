package com.sportzfy.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class FavoriteChannel(
    val id: Int = 0,
    val name: String,
    val logo: String,
    val streamUrl: String,
    val category: String
)

data class SavedPlaylist(
    val id: Int = 0,
    val title: String,
    val url: String,
    val type: String = "m3u8"
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "sportzfy.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE IF NOT EXISTS fav_channels (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            logo TEXT,
            stream_url TEXT,
            category TEXT,
            UNIQUE(name)
        )""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS playlists (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            url TEXT NOT NULL,
            type TEXT DEFAULT 'm3u8'
        )""")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS fav_channels")
        db.execSQL("DROP TABLE IF EXISTS playlists")
        onCreate(db)
    }

    // ── Favorites ────────────────────────────────────────
    fun addFavorite(ch: FavoriteChannel): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("name", ch.name); put("logo", ch.logo)
                put("stream_url", ch.streamUrl); put("category", ch.category)
            }
            writableDatabase.insertOrThrow("fav_channels", null, cv)
            true
        } catch (e: Exception) { false }
    }

    fun removeFavorite(name: String) {
        writableDatabase.delete("fav_channels", "name=?", arrayOf(name))
    }

    fun isFavorite(name: String): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT 1 FROM fav_channels WHERE name=? LIMIT 1", arrayOf(name))
        val result = cursor.moveToFirst()
        cursor.close()
        return result
    }

    fun getAllFavorites(): List<FavoriteChannel> {
        val list = mutableListOf<FavoriteChannel>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM fav_channels ORDER BY name", null)
        while (cursor.moveToNext()) {
            list.add(FavoriteChannel(
                id = cursor.getInt(0), name = cursor.getString(1),
                logo = cursor.getString(2) ?: "", streamUrl = cursor.getString(3) ?: "",
                category = cursor.getString(4) ?: ""
            ))
        }
        cursor.close()
        return list
    }

    // ── Playlists ─────────────────────────────────────────
    fun addPlaylist(pl: SavedPlaylist): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("title", pl.title); put("url", pl.url); put("type", pl.type)
            }
            writableDatabase.insertOrThrow("playlists", null, cv)
            true
        } catch (e: Exception) { false }
    }

    fun removePlaylist(id: Int) {
        writableDatabase.delete("playlists", "id=?", arrayOf(id.toString()))
    }

    fun getAllPlaylists(): List<SavedPlaylist> {
        val list = mutableListOf<SavedPlaylist>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM playlists ORDER BY title", null)
        while (cursor.moveToNext()) {
            list.add(SavedPlaylist(
                id = cursor.getInt(0), title = cursor.getString(1),
                url = cursor.getString(2), type = cursor.getString(3) ?: "m3u8"
            ))
        }
        cursor.close()
        return list
    }
}
