package com.github.antonkonyshev.stepic

import com.github.antonkonyshev.stepic.data.database.DateConverter
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

class DateConverterTestCase {
    @Test
    fun testToTimestamp() {
        val date = Date()
        assertEquals(date.time, DateConverter().dateToTimestamp(Date()))
    }

    @Test
    fun testToDate() {
        val date = Date()
        assertEquals(date, DateConverter().fromTimestamp(date.time))
    }
}