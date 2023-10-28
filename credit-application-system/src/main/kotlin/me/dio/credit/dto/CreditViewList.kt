package me.dio.credit.dto

import me.dio.credit.entity.Credit
import java.math.BigDecimal
import java.util.*

class CreditViewList(
    val creditCode: UUID,
    val creditValue: BigDecimal,
    var numberOfInstallments: Int
) {

    constructor(credit: Credit): this(
        creditCode = credit.creditCode,
        creditValue = credit.creditValue,
        numberOfInstallments = credit.numberOfInstallments
    )
}
