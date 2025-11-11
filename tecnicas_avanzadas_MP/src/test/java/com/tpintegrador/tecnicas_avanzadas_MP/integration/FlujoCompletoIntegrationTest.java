package com.tpintegrador.tecnicas_avanzadas_MP.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FlujoCompletoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void flujoCompletoRevisionVehiculo() throws Exception {
        registrarUsuario("Dueño", "dueno@example.com", "secreto1", "DUENO");
        registrarUsuario("Mecanico", "mecanico@example.com", "secreto1", "MECANICO");

        String tokenDueno = obtenerToken("dueno@example.com", "secreto1");
        String tokenMecanico = obtenerToken("mecanico@example.com", "secreto1");

        Long vehiculoId = crearVehiculo(tokenDueno);
        Long turnoId = solicitarTurno(tokenDueno, vehiculoId);

        confirmarTurno(tokenMecanico, turnoId);
        registrarRevision(tokenMecanico, turnoId);
        verificarResultado(tokenDueno, turnoId);
    }

    private void registrarUsuario(String nombre, String email, String password, String rol) throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", nombre,
                "email", email,
                "password", password,
                "rol", rol
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    private String obtenerToken(String email, String password) throws Exception {
        Map<String, Object> body = Map.of(
                "email", email,
                "password", password
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    private Long crearVehiculo(String tokenDueno) throws Exception {
        Map<String, Object> body = Map.of(
                "patente", "AC123BD",
                "marca", "Toyota",
                "modelo", "Corolla",
                "anio", 2021,
                "kilometraje", 15000
        );

        MvcResult result = mockMvc.perform(post("/vehiculos")
                        .header("Authorization", "Bearer " + tokenDueno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    private Long solicitarTurno(String tokenDueno, Long vehiculoId) throws Exception {
        LocalDateTime fecha = LocalDateTime.now().plusDays(2).withNano(0);
        Map<String, Object> body = Map.of(
                "vehiculoId", vehiculoId,
                "fechaTurno", fecha.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "observaciones", "Revisión anual"
        );

        MvcResult result = mockMvc.perform(post("/turnos")
                        .header("Authorization", "Bearer " + tokenDueno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    private void confirmarTurno(String tokenMecanico, Long turnoId) throws Exception {
        mockMvc.perform(patch("/turnos/{id}/confirmar", turnoId)
                        .header("Authorization", "Bearer " + tokenMecanico))
                .andExpect(status().isOk());
    }

    private void registrarRevision(String tokenMecanico, Long turnoId) throws Exception {
        Map<String, Object> body = Map.of(
                "puntajes", java.util.List.of(10, 10, 10, 10, 10, 10, 10, 10),
                "comentarioMecanico", "Todo en orden"
        );

        mockMvc.perform(post("/turnos/{id}/revision", turnoId)
                        .header("Authorization", "Bearer " + tokenMecanico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    private void verificarResultado(String tokenDueno, Long turnoId) throws Exception {
        MvcResult result = mockMvc.perform(get("/turnos/{id}/resultado", turnoId)
                        .header("Authorization", "Bearer " + tokenDueno))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(node.get("puntajeTotal").asInt()).isEqualTo(80);
        assertThat(node.get("calificacion").asText()).isEqualTo("SEGURO");
    }
}

