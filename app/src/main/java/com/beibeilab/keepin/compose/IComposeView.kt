package com.beibeilab.keepin.compose

import com.beibeilab.keepin.database.AccountEntity

interface IComposeView {

    fun collectAccountInfo(): AccountEntity

}