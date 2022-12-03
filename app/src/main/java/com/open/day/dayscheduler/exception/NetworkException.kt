package com.open.day.dayscheduler.exception

class NetworkException : Exception {
    constructor() : super("Network is not available")
    constructor(message: String) : super(message)
}