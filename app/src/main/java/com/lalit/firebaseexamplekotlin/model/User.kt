package com.lalit.firebaseexamplekotlin.model

import java.util.*

data class User(var name: String? = null, var email: String? = null, var id: String? = null) {

    companion object {
        @JvmField
        val NAME_VARIABLE_VALUE = "name"
        @JvmField
        val EMAIL_VARIABLE_VALUE = "email"
        @JvmField
        val ID_VARIABLE_VALUE = "id"

        fun getKeyMap(user: User): HashMap<String, Any?> {
            val hm: HashMap<String, Any?> = HashMap()
            hm.put(NAME_VARIABLE_VALUE, user?.name)
            hm.put(EMAIL_VARIABLE_VALUE, user?.email)
            hm.put(ID_VARIABLE_VALUE, user?.id)
            return hm
        }
    }

}
