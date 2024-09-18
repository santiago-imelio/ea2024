# Proyecto template para AE 2024

## Requerimientos
Este proyecto utiliza jMetal 6.2, el cual requiere Open JDK 17 y Apache Maven 3.9.9 instalados.

## Instrucciones
Clonar repositorio e ingresar al mismo
```
git clone <repository url>
...

cd test-proyecto
```
Cargar el submodulo de jMetal
```
git submodule init
git submodule update
```
Crear los archivos JAR de jMetal
```
cd jMetal
mvn package
```
Volver al root del proyecto y compilar
```
cd ..
mvn compile
```