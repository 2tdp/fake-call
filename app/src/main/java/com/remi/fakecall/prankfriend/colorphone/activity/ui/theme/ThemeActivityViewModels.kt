package com.remi.fakecall.prankfriend.colorphone.activity.ui.theme

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remi.fakecall.prankfriend.colorphone.activity.data.color.ColorModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.color.DataColor
import com.remi.fakecall.prankfriend.colorphone.activity.data.gif.DataGif
import com.remi.fakecall.prankfriend.colorphone.activity.data.gif.GifModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.picture.DataPic
import com.remi.fakecall.prankfriend.colorphone.activity.data.picture.PicModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ButtonStyleModel
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.DataTheme
import com.remi.fakecall.prankfriend.colorphone.activity.data.theme.ThemeModel
import com.remi.fakecall.prankfriend.colorphone.activity.ui.base.UiState
import com.remi.fakecall.prankfriend.colorphone.helpers.LIST_THEME
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_T_B
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
class ThemeActivityViewModels @Inject constructor(): ViewModel() {

    private val _uiStateAllColor = MutableStateFlow<UiState<MutableList<ColorModel>>>(UiState.Loading)
    val uiStateAllColor: StateFlow<UiState<MutableList<ColorModel>>> = _uiStateAllColor

    private val _uiStateAllGradient = MutableStateFlow<UiState<MutableList<ColorModel>>>(UiState.Loading)
    val uiStateAllGradient: StateFlow<UiState<MutableList<ColorModel>>> = _uiStateAllGradient

    private val _uiStateAllButton = MutableStateFlow<UiState<MutableList<ButtonStyleModel>>>(UiState.Loading)
    val uiStateAllButton: StateFlow<UiState<MutableList<ButtonStyleModel>>> = _uiStateAllButton

    private val _uiStateAppImages = MutableStateFlow<UiState<MutableList<PicModel>>>(UiState.Loading)
    val uiStateAppImages: StateFlow<UiState<MutableList<PicModel>>> = _uiStateAppImages

    private val _uiStateMyTheme = MutableStateFlow<UiState<MutableList<ThemeModel>>>(UiState.Loading)
    val uiStateMyTheme: StateFlow<UiState<MutableList<ThemeModel>>> = _uiStateMyTheme

    private val _uiStateGif = MutableStateFlow<UiState<MutableList<GifModel>>>(UiState.Loading)
    val uiStateGif: StateFlow<UiState<MutableList<GifModel>>> = _uiStateGif

    fun getAllMyTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            DataLocalManager.getListMyThemeFlow(LIST_THEME).catch {
                _uiStateMyTheme.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateMyTheme.value = UiState.Success(it)
            }
        }
    }

    fun getAllColor() {
        viewModelScope.launch(Dispatchers.IO) {
            DataColor.getAllColor().catch {
                _uiStateAllColor.value = UiState.Error(it.message.toString())
            }.collect {
                it.add(0, ColorModel(
                    Color.parseColor("#1F94C6"),
                    Color.parseColor("#1F94C6"),
                    100, TYPE_T_B, true
                ))
                _uiStateAllColor.value = UiState.Success(it)
            }
        }
    }

    fun getAllGradient() {
        viewModelScope.launch(Dispatchers.IO) {
            DataColor.getAllGradient().catch {
                _uiStateAllGradient.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateAllGradient.value = UiState.Success(it)
            }
        }
    }

    fun getAllTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            DataTheme.getAllStyleButton().catch {
                _uiStateAllButton.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateAllButton.value = UiState.Success(it)
            }
        }
    }

    fun getAppImages(dir: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DataPic.getPicAssets(dir).catch {
                _uiStateAppImages.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateAppImages.value = UiState.Success(it)
            }
        }
    }

    fun getAllGif() {
        viewModelScope.launch(Dispatchers.IO) {
            DataGif.getAllGif().catch {
                _uiStateGif.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateGif.value = UiState.Success(it)
            }
        }
    }
}