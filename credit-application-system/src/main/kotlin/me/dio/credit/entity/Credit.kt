package me.dio.credit.entity

import jakarta.persistence.*
import me.dio.credit.enumeration.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
data class Credit(
    @Column(nullable = false, unique = true) val creditCode: UUID = UUID.randomUUID(),

    @Column(nullable = false) val creditValue: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false) val dayFirstInstallment: LocalDate,

    @Column(nullable = false) var numberOfInstallments: Int = 0,

    @Enumerated @Column(nullable = false) val status: Status = Status.IN_PROGRESS,

    @ManyToOne var customer: Customer? = null,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(nullable = false) val id: Long? = null
)
