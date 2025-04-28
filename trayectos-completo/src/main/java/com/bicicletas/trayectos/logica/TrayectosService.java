package com.bicicletas.trayectos.logica;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bicicletas.trayectos.dataAccess.TrayectosRepository;
import com.bicicletas.trayectos.dataAccess.UbicacionesRepository;
import com.bicicletas.trayectos.modelo.Trayecto;
import com.bicicletas.trayectos.modelo.Ubicacion;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

// Controlador de casos de uso
// tiene métodos, uno por cada caso de uso
@Service
public class TrayectosService {

    @Autowired
    TrayectosRepository trayectos;

    @Autowired
    UbicacionesRepository ubicaciones;

    // CU001 Iniciar Trayecto
    // 1. Actor ingresa la ubicación actual
    @Transactional(value = TxType.REQUIRED)
    public UUID iniciarTrayecto(Double longitud, Double latitud) 
        throws Exception
    {

        // 2. Verifica que no exista otro trayecto activo
        Trayecto trayectoActivo = trayectos.findByEnProcesoTrue();
        if (trayectoActivo != null) {
            throw new Exception("No se puede iniciar otro trayecto mientras se tiene un trayecto activo");
        }

        // 3. Determina fecha y hora |
        LocalDateTime fechaActual = LocalDateTime.now();

        // 4. Determina un id para un nuevo trayecto |
        // 5. Almacena un nuevo trayecto con el id, fecha y hora de inicio, y longitud y latitud de ubicación inicial |
        Trayecto trayecto = new Trayecto();
        trayecto.setFechaHoraInicio(fechaActual);
        trayecto.setEnProceso(true);
        trayecto = trayectos.save(trayecto);

        // 6. Agrega una ubicación con la longitud y latitud de ubicación inicial a la trayectoria
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setFechaHora(fechaActual);
        ubicacion.setLongitud(longitud);
        ubicacion.setLatitud(latitud);
        ubicacion.setTrayecto(trayecto);
        ubicacion = ubicaciones.save(ubicacion);

        trayecto.getUbicaciones().add(ubicacion);
        trayecto = trayectos.save(trayecto);

        // 7. Retorna el id del nuevo trayecto |
        return trayecto.getId();

    }

    // CU002 : Agregar ubicación al Trayecto
    // 1. Ingresa el id del trayecto en curso
    // 4. Ingresa la longitud y la latitud de la ubicación actual
    @Transactional(value = TxType.REQUIRED)
    public UUID agregarUbicacionAlTrayecto(UUID idTrayecto, Double longitud, Double latitud) 
        throws Exception {

        // 2. verifica que exista un trayecto con ese id

        // busca el trayecto usando el parámetro `idTrayecto`
        Optional<Trayecto> optTrayecto = trayectos.findById(idTrayecto);

        // lanza una excepción si no existe el trayecto
        if (optTrayecto.isEmpty()) {
            throw new Exception("No existe un trayecto con el id " + idTrayecto);
        }

        // 3. verifica que el trayecto esté activo

        // obtiene el objeto con el trayecto actual
        Trayecto trayectoActual = optTrayecto.get();

        // lanza una excepción si el trayecto no está en proceso
        if (! trayectoActual.isEnProceso()) {
            throw new Exception("El trayecto con el id " + idTrayecto + "no está activo");
        }

        // 5. Determina fecha y hora

        // obtiene un objeto Date con la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // 6. Agrega  una nueva ubicación con fecha y hora actual 
        //    y la longitud y latitud de la ubicación al trayecto en curso

        // Crea una nueva ubicación
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setFechaHora(fechaActual);
        ubicacion.setLongitud(longitud);
        ubicacion.setLatitud(latitud);
        ubicacion.setTrayecto(trayectoActual);

        // guarda la ubicación
        ubicacion = ubicaciones.save(ubicacion);

        // agrega la ubicación al trayecto
        trayectoActual.getUbicaciones().add(ubicacion);
        trayectoActual = trayectos.save(trayectoActual);

        // returna el id
        return trayectoActual.getId();

    }


    // CU003 - Finalizar Trayecto
    // 1. Ingresa el id del trayecto en curso
    // 4. Ingresa la longitud y la latitud de la ubicación actual
    @Transactional(value = TxType.REQUIRED)    
    public UUID finalizarTrayecto(UUID idTrayecto, Double longitud, Double latitud) 
        throws Exception
    {

        // 2. verifica que exista un trayecto con ese id 

        // busca el trayecto usando el parámetro `idTrayecto`
        Optional<Trayecto> optTrayecto = trayectos.findById(idTrayecto);

        // lanza una excepción si no existe el trayecto
        if (optTrayecto.isEmpty()) {
            throw new Exception("No existe un trayecto con el id " + idTrayecto);
        }

        // 3. verifica que el trayecto esté activo 

        // obtiene el objeto con el trayecto actual
        Trayecto trayectoActual = optTrayecto.get();

        // lanza una excepción si el trayecto no está en proceso
        if (! trayectoActual.isEnProceso()) {
            throw new Exception("El trayecto con el id " + idTrayecto + "no está activo");
        }

        // 5. Determina fecha y hora 

        // obtiene un objeto Date con la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // 6. Agrega  una nueva ubicación con fecha y hora actual 
        //    y la longitud y latitud de la ubicación al trayecto en curso 

        // Crea una nueva ubicación
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setFechaHora(fechaActual);
        ubicacion.setLongitud(longitud);
        ubicacion.setLatitud(latitud);
        ubicacion.setTrayecto(trayectoActual);

        // guarda la ubicación
        ubicacion = ubicaciones.save(ubicacion);

        // asigna la fecha y hora final al trayecto
        trayectoActual.setFechaHoraFin(fechaActual);

        // agrega la ubicación al trayecto
        trayectoActual.getUbicaciones().add(ubicacion);

        // 7. Calcula la duración del trayecto 
        Long duracion = Duration.between(
                                trayectoActual.getFechaHoraInicio(), 
                                trayectoActual.getFechaHoraFin())
                            .toSeconds();


        // 8. Actualiza el trayecto grabando la hora final del trayecto, 
        //    la duración y cambiando el estado a inactivo 

        trayectoActual.setDuracion(duracion);
        trayectoActual.setEnProceso(false);
        trayectoActual = trayectos.save(trayectoActual);

        // returna el id
        return trayectoActual.getId();

    }

    // CU004 - Consultar estadísticas del trayecto
    // 1. Ingresa el id del trayecto a revisar
    @Transactional(value = TxType.SUPPORTS)
    public Trayecto consultarTrayecto(UUID idTrayecto)
        throws Exception {

        // 2. verifica que exista un trayecto con ese id
        Trayecto trayecto = trayectos.findById(idTrayecto).orElse(null);

        if (trayecto == null) {
            throw new Exception("No existe trayecto con el id " + idTrayecto);
        }

        // 3. Muestra la información de la hora de inicio, la longitud y latitud 
        //    de la ubicación inicial, la longitud y latitud de todas las ubicaciones 
        //    de ese trayecto, la distancia recorrida, la hora final y la longitud y 
        //    latitud de la última ubicación
         
        return trayecto;

    }


    // CU005- Consultar resumen de todos los trayectos
    // 1. Ingresa la fecha de inicio y la fecha fin de los trayectos a consultar 
    @Transactional(value = TxType.SUPPORTS)
    public List<Trayecto> consultarTrayectosPorFecha(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFinal)
        throws Exception {

        // 2. Verifica que la fecha de inicio sea menor que la fecha final

        if (fechaInicio.isAfter(fechaFinal)) {
            throw new Exception("La fecha de inicio es mayor que la fecha final");
        }

        // 3. Obtiene todos los trayectos en ese rango de fechas
        List<Trayecto> listaTrayectos = trayectos.findEnRangoFechas(fechaInicio, fechaFinal);

        // 4. Muestra la información todos los trayectos encontrados: la hora de inicio, 
        //    la longitud y latitud de la ubicación inicial, la longitud y latitud de 
        //    todas las ubicaciones de ese trayecto, la distancia recorrida, la hora final 
        //    y la longitud y latitud de la última ubicación |
        return listaTrayectos;

    }

}











