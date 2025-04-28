package com.bicicletas.trayectos.dataAccess;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bicicletas.trayectos.modelo.Ubicacion;

@Repository
public interface UbicacionesRepository 
    extends JpaRepository<Ubicacion, UUID>
{

}
