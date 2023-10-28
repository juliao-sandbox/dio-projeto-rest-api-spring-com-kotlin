package me.dio.credit.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.entity.Address
import me.dio.credit.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "O nome não pode estar vazio") val firstName: String,
    @field:NotEmpty(message = "O sobrenome não pode estar vazio") val lastName: String,
    @field:NotEmpty(message = "O CPF não pode estar vazio") @CPF(message = "CPF inválido") val cpf: String,
    @field:NotNull(message = "Valor da renda inválido") val income: BigDecimal,
    @field:NotEmpty(message = "O e-mail não pode estar vazio") @field:Email(message = "E-mail inválido") val email: String,
    @field:NotEmpty(message = "A senha não pode estar vazio") val password: String,
    @field:NotEmpty(message = "O CEP não pode estar vazio") val zipCode: String,
    @field:NotEmpty(message = "O endereço não pode estar vazio") val street: String
) {

    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        address = Address(
            zipCode = this.zipCode, street = this.street
        )
    )
}
