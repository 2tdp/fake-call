package com.remi.fakecall.prankfriend.colorphone.activity.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remi.fakecall.prankfriend.colorphone.activity.data.CallModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.DataMusic
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.DataRecord
import com.remi.fakecall.prankfriend.colorphone.activity.data.audio.MusicModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.contact.ContactModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.contact.DataContact
import com.remi.fakecall.prankfriend.colorphone.activity.data.picture.BucketPicModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.picture.DataPic
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_FAKE_CONTACT
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_SCHEDULE_HISTORY
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_THEME
import com.remi.fakecall.prankfriend.colorphone.sharepref.DataLocalManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _uiStateAllPicture = MutableStateFlow<UiState<MutableList<BucketPicModel>>>(UiState.Loading)
    val uiStateAllPicture: StateFlow<UiState<MutableList<BucketPicModel>>> = _uiStateAllPicture

    private val _uiStateContact = MutableStateFlow<UiState<MutableList<ContactModel>>>(UiState.Loading)
    val uiStateContact: StateFlow<UiState<MutableList<ContactModel>>> = _uiStateContact

    private val _uiStateAudio = MutableStateFlow<UiState<MutableList<MusicModel>>>(UiState.Loading)
    val uiStateAudio: StateFlow<UiState<MutableList<MusicModel>>> = _uiStateAudio

    private val _uiStateRecord = MutableStateFlow<UiState<MutableList<MusicModel>>>(UiState.Loading)
    val uiStateRecord: StateFlow<UiState<MutableList<MusicModel>>> = _uiStateRecord

    private val _uiStateMyTheme = MutableStateFlow<UiState<MutableList<ThemeModel>>>(UiState.Loading)
    val uiStateMyTheme: StateFlow<UiState<MutableList<ThemeModel>>> = _uiStateMyTheme

    private val _uiStateHistory = MutableStateFlow<UiState<MutableList<CallModel>>>(UiState.Loading)
    val uiStateHistory: StateFlow<UiState<MutableList<CallModel>>> = _uiStateHistory

    fun getAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            DataLocalManager.getListHistorySchedule(LIST_SCHEDULE_HISTORY).catch {
                _uiStateHistory.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateHistory.value = UiState.Success(it)
            }
        }
    }

    fun getAllMyTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            DataLocalManager.getListMyThemeFlow(LIST_THEME).catch {
                _uiStateMyTheme.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateMyTheme.value = UiState.Success(it)
            }
        }
    }

    fun getAllPic() {
        viewModelScope.launch(Dispatchers.IO) {
            DataPic.getBucketPictureList().catch {
                _uiStateAllPicture.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateAllPicture.value = UiState.Success(it)
            }
        }
    }

    fun getAllContact() {
        viewModelScope.launch(Dispatchers.IO) {
            DataContact.getAllContact().catch {
                _uiStateContact.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateContact.value = UiState.Success(it)
            }
        }
    }

    fun getAllMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            DataMusic.getSongList().catch {
                _uiStateAudio.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateAudio.value = UiState.Success(it)
            }
        }
    }

    fun getAllRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            DataRecord.getRecordSave().catch {
                _uiStateRecord.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateRecord.value = UiState.Success(it)
            }
        }
    }
}