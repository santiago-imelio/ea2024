# Proyecto AE 2024

## Requerimientos
Este proyecto utiliza jMetal 6.2 y jMetal 1.5.3, los cuales requieren Open JDK 17 y Apache Maven 3.9.9 instalados.

## Instrucciones
Clonar repositorio e ingresar al mismo
```
git clone <repository url>
...

cd test-proyecto
```
Cargar los submodulos de jMetal y jGraphT
```
git submodule init
git submodule update
```
Y ejecutar el script `build.sh` que va a crear los JARs necesarios y compilar el proyecto.
```
bash build.sh
```