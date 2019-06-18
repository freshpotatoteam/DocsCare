package com.ddd.docscare.base

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

enum class PP {
    IS_FIRST_RUN, // 앱 최초 실행 여부
    PUSH_TOKEN,
    IS_READ_PUSH,
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    USER_ID,
    ;

    companion object {
        private lateinit var PREFERENCES: SharedPreferences

        private const val DEFAULT_STRING = ""
        private const val DEFAULT_FLOAT = -1f
        private const val DEFAULT_INT = -1
        private const val DEFAULT_LONG = -1L
        private const val DEFAULT_BOOLEAN = false

        fun CREATE(context: Context) {
            PREFERENCES = PreferenceManager.getDefaultSharedPreferences(context)
        }

        //실재값에 변화가 있을때만 event가 날라온다
        fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PREFERENCES.registerOnSharedPreferenceChangeListener(listener)
        }

        fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            PREFERENCES.unregisterOnSharedPreferenceChangeListener(listener)
        }

        fun clear() = PREFERENCES.edit().clear().commit()

        val deviceid: String get() = PREFERENCES.run { getString("deviceid", null) ?: java.util.UUID.randomUUID().toString().also { edit().putString("deviceid", it).apply() } }
        var user_id: String
            get() = PREFERENCES.run { getString("user_id", null) ?: deviceid }
            set(user_id) = PREFERENCES.edit().putString("user_id", user_id).apply()
    }


    fun getBoolean   (DEFAULT : Boolean      = DEFAULT_BOOLEAN )                =  PREFERENCES.getBoolean  (name, DEFAULT)
    fun isit         (DEFAULT : Boolean      = DEFAULT_BOOLEAN )                =              getBoolean  (      DEFAULT)
    fun getInt       (DEFAULT : Int          = DEFAULT_INT     )                =  PREFERENCES.getInt      (name, DEFAULT)
    fun getLong      (DEFAULT : Long         = DEFAULT_LONG    )                =  PREFERENCES.getLong     (name, DEFAULT)
    fun getFloat     (DEFAULT : Float        = DEFAULT_FLOAT   )                =  PREFERENCES.getFloat    (name, DEFAULT)
    fun getString    (DEFAULT : String?      = DEFAULT_STRING  ) : String?      =  PREFERENCES.getString   (name, DEFAULT)
    fun get          (DEFAULT : String?      = DEFAULT_STRING  ) : String?      =              getString   (      DEFAULT)
    fun getStringSet (DEFAULT : Set<String>? = null            ) : Set<String>? = PREFERENCES.getStringSet(name, DEFAULT)

    fun set(v: Boolean     ) = PREFERENCES.edit().putBoolean(name, v).apply()
    fun set(v: Int         ) = PREFERENCES.edit().putInt(name, v).apply()
    fun set(v: Long        ) = PREFERENCES.edit().putLong(name, v).apply()
    fun set(v: Float       ) = PREFERENCES.edit().putFloat(name, v).apply()
    fun set(v: String     ?) = PREFERENCES.edit().putString(name, v).apply()
    fun set(v: Set<String>?) = PREFERENCES.edit().putStringSet(name, v).apply()

    fun toggle() = set(!getBoolean())
    fun contain() = PREFERENCES.contains(name)
    fun remove() = PREFERENCES.edit().remove(name).commit()

    fun first(): Boolean  = getBoolean().apply { set(true) }
}
