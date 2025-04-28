# Implementando Casos de Uso en una aplicación web en Spring

> En este tutorial implementará unos casos de uso en una aplicación web desarrollada en [Spring Boot](https://spring.io/projects/spring-boot). 
> Para implementar cada caso de uso es necesario implementar métodos que ejecutan el caso de uso y métodos que implementan pruebas automatizadas. 

---

## Objetivos de aprendizaje

En este tutorial, aprenderá a 
- Desarrollar métodos que implementan casos de uso
- Desarrollar métodos que implementan pruebas automatizadas para los casos de uso


## Requisitos previos

- Conocimientos básicos de casos de uso
- Conocimientos básicos de programación, Java y bases de datos.
- Conocimientos básicos de Spring Boot
- Un entorno de desarrollo para Java
  - Un computador Windows, Mac o Linux, con [Docker Desktop](https://www.docker.com/products/docker-desktop/) y [Visual Studio Code](https://code.visualstudio.com/) instalado. 
  - Una instancia de [Github Codespaces](https://github.com/features/codespaces), [Firebase Studio](https://firebase.google.com/docs/studio) o [CodeSandbox](https://codesandbox.io/dashboard)



## Instrucciones para trabajar en el proyecto

### Para trabajar en su computador

1. Navegue hasta el repositorio del proyecto en Github
    - [https://github.com/sw-labs/springboot-usecase-tutorial](https://github.com/sw-labs/springboot-usecase-tutorial)

2. Cree una copia del repositorio usando el botón "Use this Template", la opción "Create a new repository"
    - asigne un nombre al nuevo repositorio

3. Haga una copia local del proyecto en su computadora usando el comando `git clone`

    ```
    # Si el repositorio es https://github.com/ejemplo/spring
    # Ejecute los comandos:

    git clone https://github.com/ejemplo/spring
    cd spring
    ```

4. Inicie Visual Studio Code usando el comando `code` 

    ```
    code . 
    ```

5. Al iniciar Visual Studio Code, ejecute el proyecto en el devcontainer.
    * Ejecute `Dev Containers: Open in Container`

### Para trabajar desde Internet

- Ejecute el entorno de desarrollo cuando alguno de estos comandos

    - [Ejecutar en Firebase Studio](https://idx.google.com/new?template=https://github.com/sw-labs/springboot-usecase-tutorial)
    - [Ejecutar en Github Codespaces](https://github.com/codespaces/new?skip_quickstart=true&machine=standardLinux32gb&repo=792310042&ref=main&devcontainer_path=.devcontainer%2Fdevcontainer.json&geo=UsEast)


## Instrucciones

1. Revise el modelo de clases del proyecto
    * [clases-dominio.md](docs/clases-dominio.md)
    * [clases-diseno.md](docs/clases-diseno.md)

2. Revise la documentación de los casos de uso

    * [CU001-iniciar-trayecto.md](docs/use-cases/CU001-iniciar-trayecto.md)
    * [CU002-registrar-ubicacion.md](docs/use-cases/CU002-registrar-ubicacion.md)
    * [CU003-finalizar-trayecto.md](docs/use-cases/CU003-finalizar-trayecto.md)
    * [CU004-consultar-trayecto.md](docs/use-cases/CU004-consultar-trayecto.md)
    * [CU005-consultar-resumen-trayectos.md](docs/use-cases/CU005-consultar-resumen-trayectos.md)

3. Ejecute el Code Tour de `Código Inicial` y revise el código existente en el proyecto.

4. Ejecute el Code Tour de `Paso a Paso` y modifique el proyecto para implementar el caso de uso CU002

5. Implemente los otros casos de uso CU003, CU004 y CU005.


## Solución

Se puede ver una solución parcial al taller en el proyecto `trayectos-completo`.

1. Para revisar y ejecutar el proyecto, es posible agregar el módulo en el archivo `pom.xml` en la carpeta raíz.

    ```
        <modules>
            <module>trayectos</module>
                <module>trayectos-completo</module>
        </modules>
    ```

