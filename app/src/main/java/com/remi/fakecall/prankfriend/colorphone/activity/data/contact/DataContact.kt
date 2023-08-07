package com.remi.fakecall.prankfriend.colorphone.activity.data.contact

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.remi.fakecall.prankfriend.colorphone.MyApp
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_FAKE_CONTACT
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DataContact {

    @SuppressLint("Range")
    fun getAllContact(): Flow<MutableList<ContactModel>> {

        val lstContact = mutableListOf<ContactModel>()
        var tmpContact = ContactModel()

        if (ActivityCompat.checkSelfPermission(MyApp.ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(MyApp.ctx, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            val cur = MyApp.ctx.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null
            )
            cur?.let {
                if (it.count > 0) {
                    while (it.moveToNext()) {
                        val id =
                            it.getString(it.getColumnIndex(ContactsContract.Data.NAME_RAW_CONTACT_ID))
                        val name =
                            it.getString(it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                        val number =
                            it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val thumb = it.getString(it.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI))
                        val ringtone =
                            it.getString(it.getColumnIndex(ContactsContract.Data.CUSTOM_RINGTONE))
                        val lookupKey = it.getString(it.getColumnIndex(ContactsContract.Data.LOOKUP_KEY))

                        if (id != tmpContact.id) {

                            val contact =
                                ContactModel(id, name, thumb ?: "", number, ringtone ?: "", lookupKey, false, false)
                            if (lstContact.isEmpty()) {
                                contact.isTitle = true
                                lstContact.add(contact)
                            } else lstContact.add(contact)
                            tmpContact = contact
                        }
                    }
                }
            }
            cur?.close()
        }

        val lstFakeContact = DataLocalManager.getListFakeContact(LIST_FAKE_CONTACT)
        if (lstFakeContact.isNotEmpty()) {
            lstFakeContact[0].isTitle = true
            lstContact.addAll(0, lstFakeContact)
        }

        return flow {
            emit(lstContact)
        }
    }
}