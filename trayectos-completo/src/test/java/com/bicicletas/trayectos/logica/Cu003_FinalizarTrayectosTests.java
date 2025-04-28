package com.bicicletas.trayectos.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bicicletas.trayectos.dataAccess.TrayectosRepository;
import com.bicicletas.trayectos.dataAccess.UbicacionesRepository;
import com.bicicletas.trayectos.modelo.Trayecto;

import jakarta.transaction.Transactional;

@SpringBootTest
public class Cu003_FinalizarTrayectosTests {

	@Autowired
	TrayectosService servicio;

	@Autowired
	TrayectosRepository trayectos;

	@Autowired
	UbicacionesRepository ubicaciones;

	@Test
	@Transactional
	void finalizaTrayecto_almacenaUbicacionYActualizaTrayecto() {

		try {

		// -- Arrange: Prepara la prueba

			// inicia un trayecto
			UUID idTrayecto = servicio.iniciarTrayecto(27.0, 42.0);

		// -- Act: Ejecuta la operación que se debe probar	

			// termina el trayecto
			servicio.finalizarTrayecto(idTrayecto, 27.2, 43.2);

		// -- Assert: Revisa el resultado

			// obtiene un trayecto actualizado
			Trayecto trayectoActualizado = trayectos.findById(idTrayecto).get();

			// revisa que el trayecto tenga dos ubicaciones
			assertEquals(2, trayectoActualizado.getUbicaciones().size(), "El trayecto no se actualiza y queda con dos ubicaciones");

			// revisa que el trayecto aparezca como no activo
			assertFalse(trayectoActualizado.isEnProceso(), "El trayecto aparece como no activo");

			// revisa que la duración no es nulo
			assertNotNull(trayectoActualizado.getDuracion(), "El trayecto aparece con duración en null");

		} catch (Exception e) {
			fail("Genero excepción y no debería", e);
		}

	}


	@Test
    @Transactional
    void finalizarTrayectoInexistente_Falla() {

		try {

		// -- Arrange: Prepara la prueba

			// No es necesario hacer nada

		// -- Act: Ejecuta la operación que se debe probar

			// finalizar trayecto invocando al caso de uso
			UUID idTrayecto = UUID.randomUUID();
			servicio.finalizarTrayecto(idTrayecto, 27.2, 43.2);

		// -- Assert: Revisa el resultado

			// la prueba falla si no se genera una excepción
			fail("No generó excepción al finalizar un trayecto inexistente");

		} catch (Exception e) {
			// ok -- debía generar excepcion
		}
				
	}

	@Test
    @Transactional
    void finalizarTrayectoInactivo_Falla() {

		try {

		// -- Arrange: Prepara la prueba

			// Crea un trayecto
			UUID idTrayecto = servicio.iniciarTrayecto(27.0, 42.0);

			// lee el trayecto de la base de datos
			Trayecto trayecto = trayectos.findById(idTrayecto).orElse(null);
			assertNotNull(trayecto, "No se pudo leer el trayecto recien creado");

			// lo cambia a inactivo
			trayecto.setEnProceso(false);
			trayectos.save(trayecto);

		// -- Act: Ejecuta la operación que se debe probar

			// Finaliza ese trayecto invocando al caso de uso
			servicio.finalizarTrayecto(idTrayecto, 27.2, 43.2);


		// -- Assert: Revisa el resultado

			// la prueba falla si no se genera una excepción
			fail("No generó excepción al finalizar un trayecto inactivo");

		} catch (Exception e) {
			// ok -- debía generar excepcion
		}
				
	}

}
