package com.example.lesson24

import java.util.concurrent.Executors

object AppExecutors {
    val networkExecutor = Executors.newFixedThreadPool(4)
    val cpuBoundExecutors = Executors.newFixedThreadPool(2)
    val single = Executors.newSingleThreadExecutor()
}