package org.mb.rps

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.web.filter.ShallowEtagHeaderFilter

@SpringBootApplication
class RockpaperscissorsApplication {
    @Bean
    fun shallowEtagHeaderFilter() = FilterRegistrationBean(ShallowEtagHeaderFilter()).apply {
        addUrlPatterns("/*")
        setName("eTagFilter")
    }
}

fun main(args: Array<String>) {
    runApplication<RockpaperscissorsApplication>(*args)
}
