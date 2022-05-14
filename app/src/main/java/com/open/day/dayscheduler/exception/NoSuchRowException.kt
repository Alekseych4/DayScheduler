package com.open.day.dayscheduler.exception

import java.lang.RuntimeException

class NoSuchRowException : RuntimeException {
    constructor() : super("DB error!")
    constructor(message: String) : super(message)
}