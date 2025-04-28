package com.bicicletas.trayectos.dataAccess;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bicicletas.trayectos.modelo.Trayecto;
import java.util.List;
import java.time.LocalDateTime;


@Repository
public interface TrayectosRepository 
    extends JpaRepository<Trayecto, UUID>
{

    // obtiene el trayecto con "enProceso == true"")
    Trayecto findByEnProcesoTrue();

    // obtiene los trayectos entre fecha inico y fecha fin
    @Query("select t from Trayecto t where t.fechaHoraInicio >= :fechaHoraInicio and t.fechaHoraInicio <= :fechaHoraFin")
    List<Trayecto> findEnRangoFechas(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin);

}
