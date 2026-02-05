package com.example.pricing_service.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1_At10AMOn14th_ShouldReturnPrice35_50() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.finalPrice").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test2_At4PMOn14th_ShouldReturnPrice25_45() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.finalPrice").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test3_At9PMOn14th_ShouldReturnPrice35_50() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T21:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.finalPrice").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test4_At10AMOn15th_ShouldReturnPrice30_50() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-15T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.finalPrice").value(30.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test5_At9PMOn16th_ShouldReturnPrice38_95() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-16T21:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.finalPrice").value(38.95))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturn404WhenNoPriceFound() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2019-01-01T10:00:00")
                        .param("productId", "99999")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenMissingRequiredParameter() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WithErrorBodyWhenNoPriceFound() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2025-01-01T10:00:00")
                        .param("productId", "123")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRICE_NOT_FOUND"))
                // Validamos que el mensaje del 404 incluya los datos de la consulta
                .andExpect(jsonPath("$.message").value(containsString("productId=123")))
                .andExpect(jsonPath("$.message").value(containsString("brandId=1")));
    }

    @Test
    void shouldReturn400WithErrorBodyWhenMissingParameter() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")) // Falta brandId
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"))
                // Usamos containsString para que el test no sea frágil pero valide el origen del error
                .andExpect(jsonPath("$.message").value(containsString("brandId")))
                .andExpect(jsonPath("$.path").value("/prices"));
    }

    @Test
    void shouldReturn400WhenInvalidParameterType() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "NOT_A_NUMBER")) // "NOT_A_NUMBER" provocará el error de tipo
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.message").value(containsString("brandId")))
                .andExpect(jsonPath("$.message").value(containsString("Long")));
    }

    @Test
    void shouldReturn400WhenBrandIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "abc")) // "abc" causará TypeMismatch
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.message").value(containsString("brandId")));
    }

    @Test
    @DisplayName("Should return 400 when applicationDate has invalid format")
    void shouldReturn400WhenInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14-10:00") // Formato incorrecto
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.message").value(containsString("applicationDate")))
                .andExpect(jsonPath("$.path").value("/prices"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 400 when productId is not a number")
    void shouldReturn400WhenProductIdIsInvalid() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "not-a-number")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.message").value(containsString("productId")))
                .andExpect(jsonPath("$.message").value(containsString("Long")));
    }

    @Test
    @DisplayName("Should return 400 when productId is missing")
    void shouldReturn400WhenProductIdIsMissing() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("brandId", "1"))
                // No enviamos productId
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"))
                .andExpect(jsonPath("$.message").value(containsString("productId")));
    }

    @Test
    @DisplayName("Should return 400 when applicationDate is only a date without time")
    void shouldReturn400WhenDateHasNoTime() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14") // Falta la parte 'T00:00:00'
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.message").value(containsString("applicationDate")))
                .andExpect(jsonPath("$.message").value(containsString("LocalDateTime")));
    }

}