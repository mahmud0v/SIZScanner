package uz.duol.sizscanner.data.sharedpreference

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class AppSharedPreference(@ApplicationContext context:Context) : SharedPreference(context){

    var token: String by StringPreference("")
    var refreshToken: String by StringPreference("")
}