package com.bicicletas.trayectos.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class Cu004_ConsultarTrayectoTests {

	@Autowired
	TrayectosService servicio;

	@Autowired
	TrayectosRepository trayectos;

	@Autowired
	UbicacionesRepository ubicaciones;

    @Test
    @Transactional
    void consultarTrayecto_retornaTrayecto() {

		try {

		// -- Arrange: Prepara la prueba

			// inicia un trayecto
			UUID idTrayecto = servicio.iniciarTrayecto(27.0, 42.0);

		// -- Act: Ejecuta la operación que se debe probar	

			// consulta el trayecto acabado de iniciar
			Trayecto trayecto = servicio.consultarTrayecto(idTrayecto);

		// -- Assert: Revisa el resultado

			// revisa que el trayecto tenga una sola ubicación
			assertEquals(1, trayecto.getUbicaciones().size(), "El trayecto no se actualiza y queda con dos ubicaciones");

			// revisa que el trayecto aparezca como no activo
			assertTrue(trayecto.isEnProceso(), "El trayecto aparece como no activo");


		} catch (Exception e) {
			fail("Genero excepción y no debería", e);
		}        

    }

    @Test
    @Transactional
    void consultarTrayectoFinalizado_retornaTrayecto() {

		try {

		// -- Arrange: Prepara la prueba

			// inicia un trayecto
			UUID idTrayecto = servicio.iniciarTrayecto(27.0, 42.0);
            // finaliza el trayecto
            servicio.finalizarTrayecto(idTrayecto, 28.0, 43.0);

		// -- Act: Ejecuta la operación que se debe probar	

			// consulta un trayecto finalizado
			Trayecto trayecto = servicio.consultarTrayecto(idTrayecto);

		// -- Assert: Revisa el resultado

			// revisa que el trayecto tenga dos ubicaciones
			assertEquals(2, trayecto.getUbicaciones().size(), "El trayecto no se actualiza y queda con dos ubicaciones");

			// revisa que el trayecto aparezca como no activo
			assertFalse(trayecto.isEnProceso(), "El trayecto aparece como no activo");

			// revisa que la duración no es nulo
			assertNotNull(trayecto.getDuracion(), "El trayecto aparece con duración en null");

		} catch (Exception e) {
			fail("Genero excepción y no debería", e);
		}        

    }

    @Test
    @Transactional
    void consultarTrayectoInexistente_Falla() {

		try {

            // -- Arrange: Prepara la prueba
    
                // No es necesario hacer nada
    
            // -- Act: Ejecuta la operación que se debe probar
    
                // consulta el trayecto invocando al caso de uso
                UUID idTrayecto = UUID.randomUUID();
                Trayecto trayecto = servicio.consultarTrayecto(idTrayecto);
    
            // -- Assert: Revisa el resultado
    
                assertNull(trayecto, "Retorna un proyecto cuando se buscó un id inexistente");

                // la prueba falla si no se genera una excepción
                fail("No generó excepción al consultar un trayecto inexistente");
    
            } catch (Exception e) {
                // ok -- debía generar excepcion
            }

    }

}
