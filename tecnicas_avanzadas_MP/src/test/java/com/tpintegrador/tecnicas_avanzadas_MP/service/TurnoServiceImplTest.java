package com.tpintegrador.tecnicas_avanzadas_MP.service;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Turno;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Vehiculo;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.TurnoRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.UsuarioRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.service.impl.TurnoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceImplTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private VehiculoService vehiculoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TurnoServiceImpl turnoService;

    private Usuario dueno;
    private Usuario mecanico;
    private Vehiculo vehiculo;
    private Turno turno;

    @BeforeEach
    void setUp() {
        dueno = Usuario.builder()
                .id(1L)
                .nombre("Due")
                .email("due@example.com")
                .password("pass")
                .rol(Rol.DUENO)
                .activo(true)
                .build();

        mecanico = Usuario.builder()
                .id(2L)
                .nombre("Mec")
                .email("mec@example.com")
                .password("pass")
                .rol(Rol.MECANICO)
                .activo(true)
                .build();

        vehiculo = Vehiculo.builder()
                .id(5L)
                .patente("ABC123")
                .marca("Ford")
                .modelo("Fiesta")
                .anio(2020)
                .kilometraje(50000)
                .dueno(dueno)
                .build();

        turno = Turno.builder()
                .id(10L)
                .vehiculo(vehiculo)
                .fechaTurno(LocalDateTime.now().plusDays(5))
                .confirmado(false)
                .build();
    }

    @Test
    void solicitarTurnoCuandoVehiculoPerteneceAlDueno() {
        TurnoRequest request = new TurnoRequest(vehiculo.getId(), LocalDateTime.now().plusDays(1), null);
        when(vehiculoService.obtenerPorId(vehiculo.getId())).thenReturn(vehiculo);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(invocation -> {
            Turno t = invocation.getArgument(0);
            t.setId(15L);
            return t;
        });

        TurnoResponse response = turnoService.solicitarTurno(dueno.getId(), request);

        assertThat(response.id()).isEqualTo(15L);
        verify(turnoRepository).save(any(Turno.class));
    }

    @Test
    void solicitarTurnoCuandoVehiculoNoPerteneceAlDuenoLanzaExcepcion() {
        TurnoRequest request = new TurnoRequest(vehiculo.getId(), LocalDateTime.now().plusDays(1), null);
        Usuario otro = Usuario.builder().id(99L).rol(Rol.DUENO).activo(true).email("otro@example.com").nombre("Otro").password("p").build();
        vehiculo.setDueno(otro);
        when(vehiculoService.obtenerPorId(vehiculo.getId())).thenReturn(vehiculo);

        assertThatThrownBy(() -> turnoService.solicitarTurno(dueno.getId(), request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("pertenece");
    }

    @Test
    void confirmarTurnoAsignaMecanico() {
        when(turnoRepository.findById(turno.getId())).thenReturn(Optional.of(turno));
        when(usuarioRepository.findById(mecanico.getId())).thenReturn(Optional.of(mecanico));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TurnoResponse response = turnoService.confirmarTurno(turno.getId(), mecanico.getId());

        assertThat(response.confirmado()).isTrue();
        assertThat(response.mecanicoId()).isEqualTo(mecanico.getId());
    }

    @Test
    void listarTurnosMecanicoDevuelveResultados() {
        when(turnoRepository.findByMecanicoAsignado_Id(mecanico.getId())).thenReturn(Collections.singletonList(turno));
        turno.setMecanicoAsignado(mecanico);
        TurnoResponse response = turnoService.listarTurnosMecanico(mecanico.getId()).get(0);

        assertThat(response.id()).isEqualTo(turno.getId());
    }
}


