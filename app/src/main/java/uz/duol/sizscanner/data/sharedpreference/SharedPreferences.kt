package uz.duol.sizscanner.data.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


abstract class SharedPreference(context: Context, preferences: SharedPreferences? = null) {
    private val preference =
        preferences ?: context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    inner class StringPreference(private val defValue: String? = null) :
        ReadWriteProperty<Any, String> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String =
            preference.getString(property.name, defValue) ?: ""

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) =
            preference.edit { putString(property.name, value).apply() }
    }

    inner class BooleanPreference(private val defValue: Boolean = false) :
        ReadWriteProperty<SharedPreference, Boolean> {
        override fun getValue(thisRef: SharedPreference, property: KProperty<*>) =
            preference.getBoolean(property.name, defValue)

        override fun setValue(thisRef: SharedPreference, property: KProperty<*>, value: Boolean) =
            preference.edit { putBoolean(property.name, value).apply() }
    }

    inner class IntPreference(private val defValue: Int = 0) : ReadWriteProperty<Any, Int> {
        override fun getValue(thisRef: Any, property: KProperty<*>) =
            preference.getInt(property.name, defValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) =
            preference.edit { putInt(property.name, value).apply() }
    }
}