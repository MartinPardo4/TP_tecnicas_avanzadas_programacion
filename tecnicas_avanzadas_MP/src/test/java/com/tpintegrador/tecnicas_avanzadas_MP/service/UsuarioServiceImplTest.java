package com.tpintegrador.tecnicas_avanzadas_MP.service;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RegistroUsuarioRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.UsuarioResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.UsuarioRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.security.JwtService;
import com.tpintegrador.tecnicas_avanzadas_MP.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Martin")
                .email("martin@example.com")
                .password("hashed")
                .rol(Rol.DUENO)
                .activo(true)
                .build();
    }

    @Test
    void registrarUsuarioExitosamente() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest("Martin", "martin@example.com", "secreto", Rol.DUENO);

        when(usuarioRepository.findByEmail("martin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secreto")).thenReturn("hashed");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioResponse response = usuarioService.registrar(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("martin@example.com");
    }

    @Test
    void registrarUsuarioDuplicadoLanzaExcepcion() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest("Martin", "martin@example.com", "secreto", Rol.DUENO);
        when(usuarioRepository.findByEmail("martin@example.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.registrar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("email");
    }

    @Test
    void loginRetornaTokenCuandoCredencialesValidas() {
        LoginRequest request = new LoginRequest("martin@example.com", "secreto");

        when(usuarioRepository.findByEmail("martin@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secreto", "hashed")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("token");

        LoginResponse response = usuarioService.login(request);

        assertThat(response.token()).isEqualTo("token");
        assertThat(response.userId()).isEqualTo(1L);
    }

    @Test
    void loginConPasswordInvalidoLanzaExcepcion() {
        LoginRequest request = new LoginRequest("martin@example.com", "mala");

        when(usuarioRepository.findByEmail("martin@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciales");
    }
}


