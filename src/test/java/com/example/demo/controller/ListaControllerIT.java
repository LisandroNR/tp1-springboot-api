package com.example.demo.controller;

import com.example.demo.dominio.Rol;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistroRequest;
import com.example.demo.model.Lista;
import com.example.demo.model.UsuarioEntity;
import com.example.demo.repository.UsuarioJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ListaControllerIT {

    // 1. Levantamos un PostgreSQL efímero usando Testcontainers
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // 2. Inyectamos las credenciales de este contenedor temporal a Spring Boot
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    // --- MÉTODOS AUXILIARES ---
    private void registrarUsuario(String email, String password) throws Exception {
        RegistroRequest registro = new RegistroRequest();
        registro.setEmail(email);
        registro.setPassword(password);
        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)));
    }

    private String login(String email, String password) throws Exception {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail(email);
        loginReq.setPassword(password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String registrarYLogin(String email, String password) throws Exception {
        registrarUsuario(email, password);
        return login(email, password);
    }

    // --- PRUEBAS SOLICITADAS EN LA CONSIGNA ---

    @Test
    void login_conCredencialesValidas_devuelve200YToken() throws Exception {
        registrarUsuario("valido@test.com", "123456");
        LoginRequest login = new LoginRequest();
        login.setEmail("valido@test.com");
        login.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_conCredencialesInvalidas_devuelve401() throws Exception {
        registrarUsuario("invalido@test.com", "123456");
        LoginRequest login = new LoginRequest();
        login.setEmail("invalido@test.com");
        login.setPassword("mal_password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerListas_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accederLista_propia_devuelve200() throws Exception {
        String token = registrarYLogin("propia@test.com", "123456");

        // Creamos una lista
        Lista nueva = new Lista();
        nueva.setNombre("Lista Propia");
        MvcResult result = mockMvc.perform(post("/api/listas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Integer id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asInt();

        // Accedemos a la lista que acabamos de crear
        mockMvc.perform(get("/api/listas/" + id)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accederLista_ajena_devuelve403() throws Exception {
        String tokenUser1 = registrarYLogin("user1@test.com", "123");
        String tokenUser2 = registrarYLogin("user2@test.com", "123");

        // User 1 crea una lista
        Lista nueva = new Lista();
        nueva.setNombre("Lista Ajena");
        MvcResult result = mockMvc.perform(post("/api/listas")
                .header("Authorization", "Bearer " + tokenUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andReturn();

        Integer id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asInt();

        // User 2 intenta acceder y falla (403 Forbidden)
        mockMvc.perform(get("/api/listas/" + id)
                .header("Authorization", "Bearer " + tokenUser2))
                .andExpect(status().isForbidden());
    }

    @Test
    void accederLista_ajenaComoAdmin_devuelve200() throws Exception {
        String tokenComun = registrarYLogin("comun@test.com", "123");

        // Creamos un admin y le seteamos el rol a la fuerza en la DB
        registrarUsuario("admin@test.com", "123");
        UsuarioEntity admin = usuarioJpaRepository.findByEmail("admin@test.com").get();
        admin.setRol(Rol.ADMIN);
        usuarioJpaRepository.save(admin);
        
        // Obtenemos el token AHORA que ya es ADMIN
        String tokenAdmin = login("admin@test.com", "123");

        // User común crea una lista
        Lista nueva = new Lista();
        nueva.setNombre("Lista del Comun");
        MvcResult result = mockMvc.perform(post("/api/listas")
                .header("Authorization", "Bearer " + tokenComun)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andReturn();

        Integer id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asInt();

        // El ADMIN accede a la lista del usuario común sin problemas
        mockMvc.perform(get("/api/listas/" + id)
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }
}
