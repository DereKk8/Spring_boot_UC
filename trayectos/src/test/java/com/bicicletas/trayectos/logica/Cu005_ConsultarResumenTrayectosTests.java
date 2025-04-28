package com.bicicletas.trayectos.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bicicletas.trayectos.dataAccess.TrayectosRepository;
import com.bicicletas.trayectos.dataAccess.UbicacionesRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
class Cu005_ConsultarResumenTrayectosTests {

    @Autowired
    TrayectosService servicio;

    @Autowired
    TrayectosRepository trayectos;

    @Autowired
    UbicacionesRepository ubicaciones;

    @Test
    @Transactional
    void consultarResumenTrayectos_exitoso() {
        try {
            // -- Arrange: Prepara la prueba
            // Crear dos trayectos finalizados
            UUID trayecto1Id = servicio.iniciarTrayecto(27.0, 42.0);
            servicio.registrarUbicacion(trayecto1Id, 27.1, 42.1);
            servicio.finalizarTrayecto(trayecto1Id, 27.2, 42.2);

            UUID trayecto2Id = servicio.iniciarTrayecto(28.0, 43.0);
            servicio.finalizarTrayecto(trayecto2Id, 28.1, 43.1);

            LocalDateTime inicio = LocalDateTime.now().minusDays(1);
            LocalDateTime fin = LocalDateTime.now().plusDays(1);

            // -- Act: Ejecuta la operación que se debe probar
            String resultado = servicio.consultarResumenTrayectos(inicio, fin);

            // -- Assert: Revisa el resultado
            assertNotNull(resultado, "No retornó información del resumen");
            
            // Verifica que la salida contenga la información requerida
            assertTrue(resultado.contains("Resumen de Trayectos"), "No muestra el título del resumen");
            assertTrue(resultado.contains("Total trayectos: 2"), "No muestra el total de trayectos encontrados");
            
            // Verifica información del primer trayecto
            assertTrue(resultado.contains("(27.0, 42.0)"), "No muestra la ubicación inicial del primer trayecto");
            assertTrue(resultado.contains("(27.2, 42.2)"), "No muestra la ubicación final del primer trayecto");
            
            // Verifica información del segundo trayecto
            assertTrue(resultado.contains("(28.0, 43.0)"), "No muestra la ubicación inicial del segundo trayecto");
            assertTrue(resultado.contains("(28.1, 43.1)"), "No muestra la ubicación final del segundo trayecto");

        } catch (Exception e) {
            fail("Generó excepción y no debería: " + e.getMessage());
        }
    }

    @Test
    @Transactional
    void consultarResumenTrayectos_fechaInicioMayorQueFinal() {
        try {

            // -- Arrange: Prepara la prueba
            // Crea fechas de inicio y fin donde la fecha de inicio es mayor que la fecha fin
            LocalDateTime fechaInicio = LocalDateTime.now();
            LocalDateTime fechaFin = fechaInicio.minusDays(1); // Un día antes

            // -- Act: Intenta consultar con fecha inicio > fecha fin
            servicio.consultarResumenTrayectos(fechaInicio, fechaFin);

            fail("Debería haber fallado porque la fecha inicio es mayor que la fecha fin");

        } catch (Exception e) {
            // -- Assert
            // ok
            assertEquals("La fecha de inicio debe ser menor a la fecha final", e.getMessage());
        }
    }

    @Test
    @Transactional
    void consultarResumenTrayectos_sinTrayectos() {
        try {
            // -- Arrange: Fechas sin trayectos
            LocalDateTime inicio = LocalDateTime.now().minusYears(1);
            LocalDateTime fin = inicio.plusDays(1);

            // -- Act: Consulta un periodo sin trayectos
            String resultado = servicio.consultarResumenTrayectos(inicio, fin);

            // -- Assert: Revisa el resultado
            assertNotNull(resultado, "No retornó información del resumen");
            assertTrue(resultado.contains("Total trayectos: 0"), "No indica que no hay trayectos en el periodo");

        } catch (Exception e) {
            fail("Generó excepción y no debería: " + e.getMessage());
        }
    }
}