package com.bicicletas.trayectos.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bicicletas.trayectos.dataAccess.TrayectosRepository;
import com.bicicletas.trayectos.dataAccess.UbicacionesRepository;
import com.bicicletas.trayectos.modelo.Trayecto;
import com.bicicletas.trayectos.modelo.Ubicacion;

import jakarta.transaction.Transactional;

@SpringBootTest
class Cu003_FinalizarTrayectoTests {

    @Autowired
    TrayectosService servicio;

    @Autowired
    TrayectosRepository trayectos;

    @Autowired
    UbicacionesRepository ubicaciones;

    @Test
    @Transactional
    void finalizarTrayecto_exitoso() {
        try {
            // -- Arrange: Prepara la prueba
            // Crear un trayecto activo primero
            UUID trayectoId = servicio.iniciarTrayecto(27.0, 42.0);
            Thread.sleep(1000); // Espera 1 segundo para asegurar duración > 0

            // -- Act: Ejecuta la operación que se debe probar
            servicio.finalizarTrayecto(trayectoId, 28.0, 43.0);

            // -- Assert: Revisa el resultado
            Optional<Trayecto> resultado = trayectos.findById(trayectoId);
            assertFalse(resultado.isEmpty(), "No se encontró el trayecto");

            Trayecto t = resultado.get();
            assertFalse(t.isEnProceso(), "El trayecto sigue apareciendo como activo");
            assertNotNull(t.getFechaHoraFin(), "No se registró la fecha de finalización");
            assertTrue(t.getDuracion() > 0, "No se calculó la duración del trayecto");

            assertEquals(2, t.getUbicaciones().size(), "No se agregó la ubicación final");
            
            // Verificar la última ubicación agregada
            Ubicacion ultimaUbicacion = t.getUbicaciones().get(1);
            assertEquals(28.0, ultimaUbicacion.getLongitud(), "La longitud final no coincide");
            assertEquals(43.0, ultimaUbicacion.getLatitud(), "La latitud final no coincide");

        } catch (Exception e) {
            fail("Generó excepción y no debería: " + e.getMessage());
        }
    }

    @Test
    @Transactional
    void finalizarTrayecto_noExiste() {
        try {
            // -- Act: Intenta finalizar un trayecto que no existe
            UUID idInexistente = UUID.randomUUID();
            servicio.finalizarTrayecto(idInexistente, 28.0, 43.0);

            fail("Debería haber fallado porque el trayecto no existe");

        } catch (Exception e) {
            // -- Assert
            // ok
            assertEquals("No se existe el trayecto al que se desea agregar la ubicación", e.getMessage());
        }
    }

    @Test
    @Transactional
    void finalizarTrayecto_noActivo() {
        try {
            // -- Arrange: Crear y finalizar un trayecto
            UUID trayectoId = servicio.iniciarTrayecto(27.0, 42.0);
            servicio.finalizarTrayecto(trayectoId, 28.0, 43.0);

            // -- Act: Intenta finalizar el mismo trayecto otra vez
            servicio.finalizarTrayecto(trayectoId, 29.0, 44.0);

            fail("Debería haber fallado porque el trayecto ya estaba finalizado");

        } catch (Exception e) {
            // -- Assert
            // ok
            assertEquals("No se puede agregar una ubicación a un trayecto no activo", e.getMessage());
        }
    }
}