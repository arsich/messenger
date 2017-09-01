package ru.arsich.messenger.vk

import android.os.Parcel
import android.os.Parcelable
import com.vk.sdk.api.model.Identifiable
import com.vk.sdk.api.model.VKApiModel
import com.vk.sdk.api.model.VKApiUser
import com.vk.sdk.api.model.VKList
import org.json.JSONObject


class VKChat: VKApiModel, Identifiable, Parcelable {
    var _id: Int = 0
    var type: String = ""
    var title: String = ""
    var admin_id: Int = 0
    var date: Int = 0
    var users: VKList<VKApiUser> = VKList()
    var body: String = ""
    var photo_100: String? = null

    constructor(`in`: Parcel) {
        this._id = `in`.readInt()
        this.type = `in`.readString()
        this.title = `in`.readString()
        this.admin_id = `in`.readInt()
        this.date = `in`.readInt()
        this.users = `in`.readParcelable(VKApiUser::class.java.classLoader)
//        this.users =  VKList<VKApiUser>().apply {
//            `in`.readTypedList(this, VKApiUser.CREATOR)
//        }
        this.body = `in`.readString()
        this.photo_100 = `in`.readString()
    }

    constructor(json: JSONObject) {
        parse(json)
    }

    override fun getId(): Int = _id

    override fun parse(json: JSONObject): VKChat {
        _id = json.optInt("id")
        type = json.optString("type")
        title = json.optString("title")
        admin_id = json.optInt("admin_id")
        date = json.optInt("date")
        title = json.optString("title")
        users = VKList(json.optJSONArray("users"), VKApiUser::class.java)
        body = json.optString("body")
        photo_100 = json.optString("photo_100")

        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(type)
        dest.writeString(title)
        dest.writeInt(admin_id)
        dest.writeInt(date)
        dest.writeParcelable(users, flags)
        dest.writeString(body)
        dest.writeString(photo_100)
    }

    override fun describeContents() = 0

    fun getAvatars():Array<String> {
        if (!photo_100.isNullOrEmpty()) {
            return arrayOf(photo_100!!)
        }
        val list: MutableList<String> = mutableListOf()
        val size = Math.min(users.size, 4)

        var index = 0
        while (index < size) {
            list.add(users[index].photo_100)
            index++
        }
        return list.toTypedArray()
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<VKChat> = object : Parcelable.Creator<VKChat> {

            override fun createFromParcel(`in`: Parcel) = VKChat(`in`)

            override fun newArray(size: Int) = arrayOfNulls<VKChat>(size)
        }
    }
}