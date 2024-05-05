package com.example.azp.data_classes

import java.util.Calendar


class Date() {
    @JvmField var year: Int = 0
    @JvmField var month: Int = 0
    @JvmField var day: Int = 0

    constructor(year: Int, month: Int, day: Int): this() {
        this.year = year
        this.month = month
        this.day = day
        require(year > 0) { "Year must be positive" }
        require(month in 1..12) { "Month must be between 1 and 12" }
        require(day in 1..31) { "Day must be between 1 and 31" }
    }

    override fun toString(): String {
        return String.format("%02d %02d %04d", day, month, year)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Date

        if (year != other.year) return false
        if (month != other.month) return false
        if (day != other.day) return false

        return true
    }

    fun getMonth(): Int{
        return month
    }

    companion object {
        fun now(): Date {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)
            return Date(year, month, day)
        }
    }
}
