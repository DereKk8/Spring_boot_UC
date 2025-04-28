package com.bicicletas.trayectos.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class Cu002_RegistrarUbicacionTests {

    @Autowired
    TrayectosService servicio;

    @Autowired
    TrayectosRepository trayectos;

    @Autowired
    UbicacionesRepository ubicaciones;

    @Test
    @Transactional
    void registrarUbicacion_exitoso() {
        try {
            // -- Arrange: Prepara la prueba
            // Crear un trayecto activo primero
            UUID trayectoId = servicio.iniciarTrayecto(27.0, 42.0);

            // -- Act: Ejecuta la operación que se debe probar
            // Registra una nueva ubicación en el trayecto
            servicio.registrarUbicacion(trayectoId, 28.0, 43.0);

            // -- Assert: Revisa el resultado
            Optional<Trayecto> resultado = trayectos.findById(trayectoId);
            assertFalse(resultado.isEmpty(), "No se encontró el trayecto");

            Trayecto t = resultado.get();
            assertEquals(2, t.getUbicaciones().size(), "No se agregó la nueva ubicación al trayecto");

            // Verificar la última ubicación agregada
            Ubicacion ultimaUbicacion = t.getUbicaciones().get(1);
            assertEquals(28.0, ultimaUbicacion.getLongitud(), "La longitud en la base de datos no coincide con el parámetro");
            assertEquals(43.0, ultimaUbicacion.getLatitud(), "La latitud en la base de datos no coincide con el parámetro");

        } catch (Exception e) {
            fail("Generó excepción y no debería: " + e.getMessage());
        }
    }

    @Test
    @Transactional
    void registrarUbicacion_trayectoNoExiste() {
        try {

            // -- Arrange: Prepara la prueba
            // no se requiere hacer nada antes de la prueba
            // -- Act: Intenta registrar ubicación con un ID que no existe
            UUID idInexistente = UUID.randomUUID();
            servicio.registrarUbicacion(idInexistente, 28.0, 43.0);

            fail("Debería haber fallado porque el trayecto no existe");

        } catch (Exception e) {
            // -- Assert
            assertEquals("No existe un trayecto con ese id", e.getMessage());
        }
    }

    @Test
    @Transactional
    void registrarUbicacion_trayectoNoActivo() {
        try {
            // -- Arrange: Crear y finalizar un trayecto
            UUID trayectoId = servicio.iniciarTrayecto(27.0, 42.0);
            Trayecto trayecto = trayectos.findById(trayectoId).get();
            trayecto.setEnProceso(false);
            trayectos.save(trayecto);

            // -- Act: Intenta registrar ubicación en un trayecto no activo
            servicio.registrarUbicacion(trayectoId, 28.0, 43.0);

            fail("Debería haber fallado porque el trayecto no está activo");

        } catch (Exception e) {
            // -- Assert
            assertEquals("El trayecto no está activo", e.getMessage());
            // ok
        }
    }
}