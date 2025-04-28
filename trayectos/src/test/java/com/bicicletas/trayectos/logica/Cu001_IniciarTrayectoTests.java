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
class Cu001_IniciarTrayectoTests {

	@Autowired
	TrayectosService servicio;

	@Autowired
	TrayectosRepository trayectos;

	@Autowired
	UbicacionesRepository ubicaciones;

	@Test
	@Transactional
	void iniciarTrayecto_almacenaTrayectoYUbicacion() {

		try {

		// -- Arrange: Prepara la prueba

			// no se require hacer nada antes de la prueba


		// -- Act: Ejecuta la operación que se debe probar		

			// ejecuta el caso de uso
			UUID id = servicio.iniciarTrayecto(27.0, 42.0);


		// -- Assert: Revisa el resultado

			// revisa si se almacenó el trayecto
			Optional<Trayecto> resultado = trayectos.findById(id);
			assertFalse(resultado.isEmpty(), "No se grabó el trayecto");

			Trayecto t = resultado.get();
			assertTrue(t.isEnProceso(), "No aparece como un trayecto activo");

			assertEquals(1, t.getUbicaciones().size(), "Agregó más de una ubicación al trayecto");

			Ubicacion u = t.getUbicaciones().get(0);
			assertEquals(27.0, u.getLongitud(), "La longitud en la base de datos no coincide con el parámetro");
			assertEquals(42.0, u.getLatitud(), "La latitud en la base de datos no coincide con el parámetro");

		} catch (Exception e) {
			fail("Genero excepción y no debería", e);
		}

		try {
			
			assertEquals(1, trayectos.count(), "Agregó más de un trayecto a la base de datos");		
			assertEquals(1, ubicaciones.count(), "Agregó más de una ubicación a la base de datos");

		} catch (Exception e) {
			fail("Genero excepción creando el trayecto y no debería", e);
		}

	}


	@Test
	@Transactional
	void iniciarTrayectoConOtroActivo_falla() {

		try {

		// -- Arrange: Prepara la prueba

			// inicia un Trayecto
			servicio.iniciarTrayecto(27.0, 42.0);


		// -- Act: Ejecuta la operación que se debe probar		

			// inicia otro trayecto ejecutando el caso de uso
			servicio.iniciarTrayecto(27.0, 42.0);


		// -- Assert: Revisa el resultado

			fail("Inició un trayecto cuando ya se tenía otro activo");

		} catch (Exception e) {
			// ok
		}

	}

}
