package com.open.day.dayscheduler.exception

class NoSuchRowException : Exception {
    constructor() : super("DB error!")
    constructor(message: String) : super(message)
}