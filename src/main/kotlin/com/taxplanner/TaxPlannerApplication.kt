package com.taxplanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class TaxPlannerApplication

fun main(args: Array<String>) {
    runApplication<TaxPlannerApplication>(*args)
} 