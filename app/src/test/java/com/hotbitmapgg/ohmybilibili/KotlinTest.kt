package com.hotbitmapgg.ohmybilibili

import org.junit.Test

/**
 * simple kotlin test
 * Created by hp on 2018/1/22.
 */
class KotlinTest {
    @Test
    fun simpleTest(){
        val i = 10
        i.minus(1).downTo(0).forEach{
            print("current value = $it")
        }
    }
}