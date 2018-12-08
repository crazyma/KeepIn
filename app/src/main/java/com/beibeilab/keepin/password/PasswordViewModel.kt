package com.beibeilab.keepin.password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasswordViewModel : ViewModel() {

    var ruleArray: BooleanArray = booleanArrayOf(false, false, false, false)
    val isNextStep = MutableLiveData<Boolean>()
    var password = MutableLiveData<String>()
    private var generator :PasswordGenerator? = null

    fun setRules(index: Int, checked: Boolean) {
        ruleArray[index] = checked
    }

    fun checkRulesExist() = ruleArray[0] || ruleArray[1] || ruleArray[2] || ruleArray[3]

    fun generatePassword() {

        if (generator == null) {
            generator = PasswordGenerator(
                8,
                ruleArray
            )
        }
        password.value = generator!!.generate()
    }

}