import os

test_dir = "app/src/test/java/com/loanmaster/pro/domain/calculator"
os.makedirs(test_dir, exist_ok=True)

emi_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class EmiCalculatorTest {
    @Test
    fun testCalculateEmi() {
        val calc = EmiCalculator()
        val res = calc.calculate("100000", "10", "1", "0", "0")
        // approx 8791 per month
        assertEquals(true, res.isValid)
        assertEquals(8791.0, res.emi, 2.0)
    }
}
"""

with open(f"{test_dir}/EmiCalculatorTest.kt", "w") as f:
    f.write(emi_test)

fd_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class FdCalculatorTest {
    @Test
    fun testCalculateFd() {
        val calc = FdCalculator()
        val res = calc.calculate("100000", "7", "1", "0", "0", "Yearly")
        assertEquals(true, res.isValid)
        // approx 107000 maturity
        assertEquals(107000.0, res.maturityValue, 200.0)
    }
}
"""
with open(f"{test_dir}/FdCalculatorTest.kt", "w") as f:
    f.write(fd_test)

rd_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class RdCalculatorTest {
    @Test
    fun testCalculateRd() {
        val calc = RdCalculator()
        val res = calc.calculate("1000", "7", "1", "0")
        assertEquals(true, res.isValid)
    }
}
"""
with open(f"{test_dir}/RdCalculatorTest.kt", "w") as f:
    f.write(rd_test)

sip_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class SipCalculatorTest {
    @Test
    fun testCalculateSip() {
        val calc = SipCalculator()
        val res = calc.calculate("1000", "12", "1", "0", "0")
        assertEquals(true, res.isValid)
    }
}
"""
with open(f"{test_dir}/SipCalculatorTest.kt", "w") as f:
    f.write(sip_test)

gst_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class GSTCalculatorTest {
    @Test
    fun testCalculateGst() {
        val calc = GstCalculator()
        val res = calc.calculate("100", "18", "Add GST")
        assertEquals(true, res.isValid)
        assertEquals(118.0, res.totalAmount, 0.1)
    }
}
"""
with open(f"{test_dir}/GSTCalculatorTest.kt", "w") as f:
    f.write(gst_test)

elig_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class LoanEligibilityCalculatorTest {
    @Test
    fun testCalculate() {
        val calc = LoanEligibilityCalculator()
        val res = calc.calculate("Home Loan", "50000", "0", false, "", "", "20", "8.5", true, "Excellent")
        assertEquals(true, res.totalIncome > 0)
    }
}
"""
with open(f"{test_dir}/LoanEligibilityCalculatorTest.kt", "w") as f:
    f.write(elig_test)

prep_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class PrepaymentCalculatorTest {
    @Test
    fun testCalculate() {
        val calc = PrepaymentCalculator()
        val res = calc.calculate("100000", "10", "5", "10000", "Tenure", "0", "0")
        assertEquals(true, res.isValid)
    }
}
"""
with open(f"{test_dir}/PrepaymentCalculatorTest.kt", "w") as f:
    f.write(prep_test)

comp_test = """package com.loanmaster.pro.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Test
import com.loanmaster.pro.domain.model.LoanOffer

class LoanComparisonCalculatorTest {
    @Test
    fun testCalculate() {
        val calc = LoanComparisonCalculator()
        val a = LoanOffer("A", "Bank A", 8.5, 5, 0, 100000.0)
        val b = LoanOffer("B", "Bank B", 8.0, 5, 0, 100000.0)
        val res = calc.calculate(a, b)
        assertEquals(true, res.hasValidInput)
    }
}
"""
with open(f"{test_dir}/LoanComparisonCalculatorTest.kt", "w") as f:
    f.write(comp_test)
    
