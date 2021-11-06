package com.chun.kmptemplate.data.mapping

fun interface Mapper<in T, out R> {
    fun apply(input: T): R
}
