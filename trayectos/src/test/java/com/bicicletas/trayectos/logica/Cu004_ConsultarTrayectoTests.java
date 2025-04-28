package com.bicicletas.trayectos.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bicicletas.trayectos.dataAccess.TrayectosRepository;
import com.bicicletas.trayectos.dataAccess.UbicacionesRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
class Cu004_ConsultarTrayectoTests {

    @Autowired
    TrayectosService servicio;

    @Autowired
    TrayectosRepository trayectos;

    @Autowired
    UbicacionesRepository ubicaciones;

    @Test
    @Transactional
    void consultarTrayecto_exitoso() {
        try {
            // -- Arrange: Prepara la prueba
            // Crear y finalizar un trayecto con varias ubicaciones
            UUID trayectoId = servicio.iniciarTrayecto(27.0, 42.0);
            servicio.registrarUbicacion(trayectoId, 27.1, 42.1);
            servicio.finalizarTrayecto(trayectoId, 27.2, 42.2);

            // -- Act: Ejecuta la operación que se debe probar
            String resultado = servicio.consultarTrayecto(trayectoId);

            // -- Assert: Revisa el resultado
            assertNotNull(resultado, "No retornó información del trayecto");
            
            // Verifica que la salida contenga la información requerida
            assertTrue(resultado.contains("Fecha/hora inicio:"), "No muestra la fecha/hora de inicio");
            assertTrue(resultado.contains("Ubicación inicial: (27.0, 42.0)"), "No muestra la ubicación inicial");
            assertTrue(resultado.contains("Ubicaciones registradas:"), "No muestra la lista de ubicaciones");
            assertTrue(resultado.contains("(27.1, 42.1)"), "No muestra la ubicación intermedia");
            assertTrue(resultado.contains("(27.2, 42.2)"), "No muestra la ubicación final");
            assertTrue(resultado.contains("Fecha/hora fin:"), "No muestra la fecha/hora de fin");
            assertTrue(resultado.contains("Duración:"), "No muestra la duración");
            assertTrue(resultado.contains("Distancia total:"), "No muestra la distancia total");
            assertTrue(resultado.contains("km"), "No muestra la unidad de distancia (km)");

        } catch (Exception e) {
            fail("Generó excepción y no debería: " + e.getMessage());
        }
    }

    @Test
    @Transactional
    void consultarTrayecto_noExiste() {
        try {
            // -- Act: Intenta consultar un trayecto que no existe
            UUID idInexistente = UUID.randomUUID();
            servicio.consultarTrayecto(idInexistente);

            fail("Debería haber fallado porque el trayecto no existe");

        } catch (Exception e) {
            // -- Assert
            // ok
            assertEquals("No se existe el trayecto que se desea consultar", e.getMessage());
        }
    }

    @Test
    @Transactional
    void consultarTrayecto_noFinalizado() {
        try {
            // -- Arrange: Crear un trayecto activo
            UUID trayectoId = servicio.iniciarTrayecto(27.0, 42.0);

            // -- Act: Intenta consultar un trayecto que no está finalizado
            servicio.consultarTrayecto(trayectoId);

            fail("Debería haber fallado porque el trayecto no está finalizado");

        } catch (Exception e) {
            // -- Assert
            // ok
            assertEquals("No se puede consultar un trayecto que no ha finalizado", e.getMessage());
        }
    }
}