package com.beibeilab.keepin.password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasswordViewModel : ViewModel() {

    var ruleArray: BooleanArray = booleanArrayOf(false, false, false, false)
    val isNextStep = MutableLiveData<Boolean>()
    var password = MutableLiveData<String>()
    var length = 8
    private var generator :PasswordGenerator? = null

    fun setRules(index: Int, checked: Boolean) {
        ruleArray[index] = checked
    }

    fun checkRulesExist() = ruleArray[0] || ruleArray[1] || ruleArray[2] || ruleArray[3]

    fun generatePassword() {

        if (generator == null) {
            generator = PasswordGenerator(
                length,
                ruleArray
            )
        }
        password.value = generator!!.generate()
    }

}