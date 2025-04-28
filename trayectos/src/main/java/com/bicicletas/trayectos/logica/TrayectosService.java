package com.bicicletas.trayectos.logica;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

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

    // CU002 Registrar Ubicación
    @Transactional(value = TxType.REQUIRED)
    public void registrarUbicacion(UUID trayectoId, Double longitud, Double latitud) 
        throws Exception
    {
        // 2. Verifica que exista un trayecto con ese id
        Trayecto trayecto = trayectos.findById(trayectoId)
            .orElseThrow(() -> new Exception("No existe un trayecto con ese id"));

        // 3. Verifica que el trayecto esté activo
        if (!trayecto.isEnProceso()) {
            throw new Exception("El trayecto no está activo");
        }

        // 5. Determina fecha y hora
        LocalDateTime fechaActual = LocalDateTime.now();

        // 6. Agrega una nueva ubicación con fecha y hora actual y la longitud y latitud
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setFechaHora(fechaActual);
        ubicacion.setLongitud(longitud);
        ubicacion.setLatitud(latitud);
        ubicacion.setTrayecto(trayecto);
        ubicacion = ubicaciones.save(ubicacion);

        trayecto.getUbicaciones().add(ubicacion);
        trayectos.save(trayecto);
    }

    // CU003 Finalizar Trayecto
    @Transactional(value = TxType.REQUIRED)
    public void finalizarTrayecto(UUID trayectoId, Double longitud, Double latitud) 
        throws Exception
    {
        // 2. Verifica que exista un trayecto con ese id
        Trayecto trayecto = trayectos.findById(trayectoId)
            .orElseThrow(() -> new Exception("No se existe el trayecto al que se desea agregar la ubicación"));

        // 3. Verifica que el trayecto esté activo
        if (!trayecto.isEnProceso()) {
            throw new Exception("No se puede agregar una ubicación a un trayecto no activo");
        }

        // 5. Determina fecha y hora actual
        LocalDateTime fechaActual = LocalDateTime.now();

        //No se usa registrarUbicacion porque no se quiere agregar la ubicación al trayecto
        // 6. Agrega una nueva ubicación con fecha y hora actual y la longitud y latitud
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setFechaHora(fechaActual);
        ubicacion.setLongitud(longitud);
        ubicacion.setLatitud(latitud);
        ubicacion.setTrayecto(trayecto);
        ubicacion = ubicaciones.save(ubicacion);

        trayecto.getUbicaciones().add(ubicacion);

        // 7. Calcula la duración del trayecto
        long duracionEnSegundos = java.time.Duration.between(
            trayecto.getFechaHoraInicio(), 
            fechaActual
        ).getSeconds();

        // 8. Actualiza el trayecto con hora final, duración y estado inactivo
        trayecto.setFechaHoraFin(fechaActual);
        trayecto.setDuracion(duracionEnSegundos);
        trayecto.setEnProceso(false);
        
        trayectos.save(trayecto);
    }

    // CU004 Consultar Trayecto
    @Transactional(value = TxType.REQUIRED)
    public String consultarTrayecto(UUID trayectoId) 
        throws Exception
    {
        // 2. Verifica que exista un trayecto con ese id
        Trayecto trayecto = trayectos.findById(trayectoId)
            .orElseThrow(() -> new Exception("No se existe el trayecto que se desea consultar"));

        // Verifica que el trayecto NO esté activo (debe estar finalizado)
        if (trayecto.isEnProceso()) {
            throw new Exception("No se puede consultar un trayecto que no ha finalizado");
        }
        
        // 3. Retorna la información formateada usando el toString personalizado
        return trayecto.toString();
    }

    // CU005 Consultar Resumen Trayectos
    @Transactional(value = TxType.REQUIRED)
    public String consultarResumenTrayectos(LocalDateTime fechaInicio, LocalDateTime fechaFin) 
        throws Exception
    {
        // 2. Verifica que la fecha de inicio sea menor que la fecha final
        if (fechaInicio.isAfter(fechaFin)) {
            throw new Exception("La fecha de inicio debe ser menor a la fecha final");
        }

        // 3. Obtiene todos los trayectos en ese rango de fechas
        List<Trayecto> trayectosEncontrados = trayectos.findEnRangoFechas(fechaInicio, fechaFin);

        // 4. Formatea la información de todos los trayectos
        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen de Trayectos\n");
        resumen.append("Período: ").append(fechaInicio).append(" a ").append(fechaFin).append("\n");
        resumen.append("Total trayectos: ").append(trayectosEncontrados.size()).append("\n\n");

        // Para cada trayecto, usa el toString personalizado que ya implementamos
        for (Trayecto t : trayectosEncontrados) {
            resumen.append("----------------------------------------\n");
            resumen.append(t.toString()).append("\n");
        }

        return resumen.toString();
    }
}





























