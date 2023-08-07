package com.remi.fakecall.prankfriend.colorphone

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

}

fun main() {
    print(searchInsert(intArrayOf(5,6,8,10), 1))
}

fun searchInsert(nums: IntArray, target: Int): Int {
    var tmp = target
    var tmp2 = target
    var count = 0
    if (target == 0 || target < nums[0]) return 0
    else if (nums.contains(target)) return nums.indexOf(target)
    else {
        do {
            tmp--
            tmp2++
            count++
            if (nums.contains(tmp)) return nums.indexOf(tmp) + if (target > nums[nums.size - 1]) 1 else count
            if (nums.contains(tmp2)) return nums.indexOf(tmp2) + count - 1
        } while (tmp > nums[0] || tmp2 < Math.pow(10.0, 4.0))
    }
    return 0
}