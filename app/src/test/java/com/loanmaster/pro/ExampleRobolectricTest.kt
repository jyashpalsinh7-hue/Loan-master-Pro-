package com.loanmaster.pro
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.feature.emi.*

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LoanMaster Pro", appName)
  }
}
