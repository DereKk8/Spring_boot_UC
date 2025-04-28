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
public class Cu002_RegistrarUbicacionTests {


	@Autowired
	TrayectosService servicio;

	@Autowired
	TrayectosRepository trayectos;

	@Autowired
	UbicacionesRepository ubicaciones;    

	@Test
    @Transactional
    void agregarUbicacion_adicionaUbicacionAlTrayecto() {

		try {

		// -- Arrange: Prepara la prueba

			// Crea un trayecto a la que se va a agregar la ubicación
			UUID idTrayecto = servicio.iniciarTrayecto(27.0, 42.0);

			// revisa si se almacenó el trayecto
			Optional<Trayecto> resultado = trayectos.findById(idTrayecto);
			assertFalse(resultado.isEmpty(), "No se grabó el trayecto");

			Trayecto trayecto = resultado.get();
			assertTrue(trayecto.isEnProceso(), "No aparece como un trayecto activo");

			assertEquals(1, trayecto.getUbicaciones().size(), "No se crea el trayecto con una sola ubicación");


		// -- Act: Ejecuta la operación que se debe probar

			// Agrega una  ubicación a ese trayecto invocando al caso de uso
			servicio.agregarUbicacionAlTrayecto(idTrayecto, 27.2, 43.2);


		// -- Assert: Revisa el resultado

			// obtiene un trayecto actualizado
			Trayecto trayectoActualizado = trayectos.findById(idTrayecto).get();

			// revisa que el trayecto tenga dos ubicaciones
			assertEquals(2, trayectoActualizado.getUbicaciones().size(), "El trayecto no se actualiza y queda con dos ubicaciones");

			// revisa que la nueva ubicación tenga los datos correctos
			Ubicacion ultimaUbicacion = trayectoActualizado.getUbicaciones().get(1);
			assertEquals(27.2, ultimaUbicacion.getLongitud());
			assertEquals(43.2, ultimaUbicacion.getLatitud());


		} catch (Exception e) {
			fail("Genera excepción agregando ubicación a un trayecto y no debería", e);
		}

    }   

 
	@Test
    @Transactional
    void agregarUbicacionTrayectoInexistente_Falla() {

		try {

		// -- Arrange: Prepara la prueba

			// No es necesario hacer nada

		// -- Act: Ejecuta la operación que se debe probar

			// Agrega una ubicación a un trayecto inexistente invocando al caso de uso
			UUID idTrayecto = UUID.randomUUID();
			servicio.agregarUbicacionAlTrayecto(idTrayecto, 27.2, 43.2);


		// -- Assert: Revisa el resultado

			// la prueba falla si no se genera una excepción
			fail("No generó excepción al agregar ubicación a un trayecto inexistente");

		} catch (Exception e) {
			// ok -- debía generar excepcion
		}
				
	}

	@Test
    @Transactional
    void agregarUbicacionTrayectoInactivo_Falla() {

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

			// Agrega una  ubicación a ese trayecto invocando al caso de uso
			servicio.agregarUbicacionAlTrayecto(idTrayecto, 27.2, 43.2);


		// -- Assert: Revisa el resultado

			// la prueba falla si no se genera una excepción
			fail("No generó excepción al agregar ubicación a un trayecto inactivo");

		} catch (Exception e) {
			// ok -- debía generar excepcion
		}
				
	}


}
