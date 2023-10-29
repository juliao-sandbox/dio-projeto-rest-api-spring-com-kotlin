package me.dio.credit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.dto.CustomerDto
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
        val fakeCustomerDto: CustomerDto  = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDto)

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Fulano"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Silva"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("12345678909"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("fulano@fulano.org"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("88000100"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Xyz"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`() {
        // given
        customerRepository.save(buildCustomerDto().toEntity())
        val fakeCustomerDto: CustomerDto  = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDto)

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andDo(MockMvcResultHandlers.print())
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
        val fakeCustomerDto: CustomerDto  = buildCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDto)

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consulte a documentação!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
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
    ) = CustomerDto(
        firstName = firstName, lastName = lastName, cpf = cpf, email = email, income = income, password = password, zipCode = zipCode, street = street
    )
}