package com.tpintegrador.tecnicas_avanzadas_MP.service;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.ResultadoRevisionResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RevisionRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Calificacion;
import com.tpintegrador.tecnicas_avanzadas_MP.model.ResultadoRevision;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Turno;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Vehiculo;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.ResultadoRevisionRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.service.impl.RevisionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevisionServiceImplTest {

    @Mock
    private ResultadoRevisionRepository resultadoRevisionRepository;

    @Mock
    private TurnoService turnoService;

    @InjectMocks
    private RevisionServiceImpl revisionService;

    private Usuario mecanico;
    private Usuario dueno;
    private Vehiculo vehiculo;
    private Turno turno;

    @BeforeEach
    void setUp() {
        mecanico = Usuario.builder()
                .id(2L)
                .nombre("Mec")
                .email("mec@example.com")
                .password("password")
                .rol(Rol.MECANICO)
                .activo(true)
                .build();

        dueno = Usuario.builder()
                .id(1L)
                .nombre("Due")
                .email("due@example.com")
                .password("password")
                .rol(Rol.DUENO)
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
                .fechaTurno(LocalDateTime.now().plusDays(1))
                .confirmado(true)
                .mecanicoAsignado(mecanico)
                .build();
    }

    @Test
    void registrarRevisionRetornaSeguroCuandoTotalMayorOIgual80() {
        when(turnoService.obtenerPorId(10L)).thenReturn(turno);
        when(resultadoRevisionRepository.save(any(ResultadoRevision.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RevisionRequest request = new RevisionRequest(
                List.of(10, 10, 10, 10, 10, 10, 10, 10),
                "Todo correcto"
        );

        ResultadoRevisionResponse response = revisionService.registrarRevision(10L, mecanico.getId(), request);

        assertThat(response.calificacion()).isEqualTo(Calificacion.SEGURO);
        assertThat(response.puntajeTotal()).isEqualTo(80);

        ArgumentCaptor<ResultadoRevision> captor = ArgumentCaptor.forClass(ResultadoRevision.class);
        verify(resultadoRevisionRepository).save(captor.capture());
        assertThat(captor.getValue().getTurno()).isEqualTo(turno);
    }

    @Test
    void registrarRevisionRetornaRechequearCuandoTotalMenorA40() {
        when(turnoService.obtenerPorId(10L)).thenReturn(turno);
        when(resultadoRevisionRepository.save(any(ResultadoRevision.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RevisionRequest request = new RevisionRequest(
                List.of(3, 4, 5, 4, 5, 5, 6, 6), // total 38
                "Frenos en mal estado"
        );

        ResultadoRevisionResponse response = revisionService.registrarRevision(10L, mecanico.getId(), request);

        assertThat(response.calificacion()).isEqualTo(Calificacion.RECHEQUEAR);
        assertThat(response.puntajeTotal()).isEqualTo(38);
    }

    @Test
    void registrarRevisionRetornaRechequearCuandoAlgunoEsMenorA5() {
        when(turnoService.obtenerPorId(10L)).thenReturn(turno);
        when(resultadoRevisionRepository.save(any(ResultadoRevision.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RevisionRequest request = new RevisionRequest(
                List.of(10, 10, 10, 4, 10, 10, 10, 10),
                "Revisión adicional en punto 4"
        );

        ResultadoRevisionResponse response = revisionService.registrarRevision(10L, mecanico.getId(), request);

        assertThat(response.calificacion()).isEqualTo(Calificacion.RECHEQUEAR);
    }

    @Test
    void registrarRevisionLanzaExcepcionSiMecanicoNoCoincide() {
        when(turnoService.obtenerPorId(10L)).thenReturn(turno);

        RevisionRequest request = new RevisionRequest(
                List.of(10, 10, 10, 10, 10, 10, 10, 10),
                "Todo correcto"
        );

        assertThatThrownBy(() -> revisionService.registrarRevision(10L, 999L, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("asignado");
    }

    @Test
    void obtenerResultadoRestringeAccesoAUsuariosNoRelacionados() {
        when(turnoService.obtenerPorId(10L)).thenReturn(turno);
        ResultadoRevision resultado = ResultadoRevision.builder()
                .id(100L)
                .puntajes(List.of(10, 10, 10, 10, 10, 10, 10, 10))
                .puntajeTotal(80)
                .calificacion(Calificacion.SEGURO)
                .comentarioMecanico("OK")
                .turno(turno)
                .build();
        assertThatThrownBy(() -> revisionService.obtenerResultado(10L, 50L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("acceso");
    }
}
