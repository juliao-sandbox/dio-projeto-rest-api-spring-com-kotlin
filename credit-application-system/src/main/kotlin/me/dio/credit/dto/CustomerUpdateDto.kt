package me.dio.credit.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.entity.Customer
import java.math.BigDecimal

data class CustomerUpdateDto(
    @field:NotEmpty(message = "O nome não pode estar vazio") val firstName: String,
    @field:NotEmpty(message = "O sobrenome não pode estar vazio") val lastName: String,
    @field:NotNull(message = "Valor da renda inválido") val income: BigDecimal,
    @field:NotEmpty(message = "O CEP não pode estar vazio") val zipCode: String,
    @field:NotEmpty(message = "O endereço não pode estar vazio") val street: String
) {

    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street
        return customer
    }

}
