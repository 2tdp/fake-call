package com.remi.fakecall.prankfriend.colorphone.activity.data.color

import android.graphics.Color
import com.remi.fakecall.prankfriend.colorphone.helpers.TYPE_T_B
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DataColor {

    fun getAllColor(): Flow<MutableList<ColorModel>> {
        val lstColor = mutableListOf<ColorModel>()
        for (s in colorArr)
            lstColor.add(ColorModel(Color.parseColor(s), Color.parseColor(s), 100, TYPE_T_B, false))

        return flow { emit(lstColor) }
    }

    fun getAllGradient(): Flow<MutableList<ColorModel>> {
        val lstColor = mutableListOf<ColorModel>()
        var direct = 0
        for (s in gradientArr) {
            lstColor.add(ColorModel(Color.parseColor(s[0]), Color.parseColor(s[1]), 100, direct, false))
            if (direct < 5) direct++
        }

        return flow { emit(lstColor) }
    }

    private val colorArr = arrayOf(
        "#00A4C1",
        "#783E67",
        "#AB4088",
        "#E83D72",
        "#E84244",
        "#D26D35",
        "#FD7B23",
        "#FFC038",
        "#FEE42D",
        "#8BBB1B",
        "#9FE481",
        "#F6E785",
        "#FAAFA5",
        "#A885EE",
        "#732092",
        "#313866",
        "#504099",
        "#974EC3",
        "#FE7BBF",
    )

    private val gradientArr = arrayOf(
        arrayOf("#9CD4FE", "#0998F4"),
        arrayOf("#FEB088", "#F27065"),
        arrayOf("#C99DF5", "#7866DF"),
        arrayOf("#FBCA48", "#F1752D"),
        arrayOf("#FE7201", "#DD093C"),
        arrayOf("#F8505F", "#A90F29"),
        arrayOf("#CB9BEE", "#B32078"),
        arrayOf("#79F9B4", "#41C57F"),
        arrayOf("#F36C96", "#703B9B"),
        arrayOf("#FEE9D1", "#F07170"),
        arrayOf("#DF968B", "#FEBA72"),
        arrayOf("#FCA0D5", "#9839D4"),
        arrayOf("#EF9B90", "#552E17"),
        arrayOf("#B577FC", "#35648B"),
        arrayOf("#FEDCAD", "#F2426D"),
        arrayOf("#FB63A7", "#99319F"),
        arrayOf("#F65263", "#0D7ADA"),
        arrayOf("#D199FE", "#6F7181"),
        arrayOf("#A1E245", "#5BA21F"),
        arrayOf("#6BB8B2", "#67F1A3"),
        arrayOf("#44CCFC", "#8124CC"),
        arrayOf("#A5E954", "#19AE93"),
        arrayOf("#FB59A0", "#FD5763"),
        arrayOf("#FD6BA9", "#7C68FB"),
        arrayOf("#FEE2AE", "#FFAC32"),
        arrayOf("#ADF0CF", "#9B99E1"),
        arrayOf("#06AECE", "#E08489"),
        arrayOf("#02DFEE", "#15157D"),
        arrayOf("#FEAEFE", "#FCE0F6"),
        arrayOf("#FB9C77", "#C1494B"),
        arrayOf("#F072A3", "#64131B"),
        arrayOf("#FEBE40", "#CA3992"),
        arrayOf("#3D4B9A", "#2D1560"),
        arrayOf("#5F09AE", "#510A57"),
        arrayOf("#FED329", "#FC6155"),
        arrayOf("#FCE76D", "#FFA800"),
        arrayOf("#C4A0F4", "#13CEBE"),
        arrayOf("#FF5491", "#8D45A3"),
        arrayOf("#D0E44C", "#7DB201"),
        arrayOf("#29F099", "#8371E2"),
        arrayOf("#3497FF", "#3B3D8B"),
        arrayOf("#63B2C3", "#F69115"),
    )
}