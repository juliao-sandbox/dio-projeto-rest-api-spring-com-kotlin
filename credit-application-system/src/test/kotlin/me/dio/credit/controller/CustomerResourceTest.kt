package me.dio.credit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.dto.CustomerDto
import me.dio.credit.dto.CustomerUpdateDto
import me.dio.credit.entity.Address
import me.dio.credit.entity.Customer
import me.dio.credit.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`() {
        // given
        val fakeCustomerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDto)

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON).content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Fulano"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Silva"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("12345678909"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("fulano@fulano.org"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("88000100"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Xyz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1)).andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`() {
        // given
        customerRepository.save(buildCustomerDto().toEntity())
        val fakeCustomerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDto)

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON).content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isConflict).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consulte a documentação!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.dao.DataIntegrityViolationException"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with firstName empty and return 400 status`() {
        // given
        val fakeCustomerDto: CustomerDto = buildCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDto)

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON).content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consulte a documentação!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status`() {
        // given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Fulano"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Silva"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("12345678909"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("fulano@fulano.org"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("88000100"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Xyz"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find customer with invalid id and return 400 status`() {
        // given
        val invalidId: Long = 2L

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consulte a documentação!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit.exception.BusinessException"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete customer by id`() {
        // given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer by id and return 400 status`() {
        // given
        val invalidId: Long = 2L

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consulte a documentação!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit.exception.BusinessException"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update a customer and return 200 status`() {
        // given
        val customer: Customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerUpdateDto: CustomerUpdateDto = buildCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON).content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Fulano2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Silva2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("12345678909"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("fulano@fulano.org"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("2000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("88000200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Xyz New"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update a customer and return 400 status`() {
        // given
        val invalidId: Long = 2L
        val customerUpdateDto: CustomerUpdateDto = buildCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=$invalidId")
                .contentType(MediaType.APPLICATION_JSON).content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consulte a documentação!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit.exception.BusinessException"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    private fun buildCustomerDto(
        firstName: String = "Fulano",
        lastName: String = "Silva",
        cpf: String = "12345678909",
        email: String = "fulano@fulano.org",
        income: BigDecimal = BigDecimal.valueOf((1000.0)),
        password: String = "password",
        zipCode: String = "88000100",
        street: String = "Rua Xyz",
    ): CustomerDto = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

    private fun buildCustomerUpdateDto(
        firstName: String = "Fulano2",
        lastName: String = "Silva2",
        income: BigDecimal = BigDecimal.valueOf((2000.0)),
        zipCode: String = "88000200",
        street: String = "Rua Xyz New",
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )
}