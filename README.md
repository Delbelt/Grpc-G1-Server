# QUICK-GUIDE - SERVER GRPC

### 1- Clonar el proyecto correctamente.

### 2- Esperar que el proyecto termine de cargar, es importante que se prepare el ambiente.

### 3- Tener instalado mvn (maven) para ejecutar los comandos necesarios durante todo el desarrollo.

### 4- Tener instalado el JDK-17 y configurado en el proyecto

![image](https://github.com/user-attachments/assets/9d0295c8-6ab0-4a6f-b8a6-b32b62247220)

### 5- Ejecutar el comando en la raiz del proyecto.

![image](https://github.com/user-attachments/assets/cd196b77-54ee-4a3f-8221-ac2aca42de53)

```bash
 mvn clean install
```
### 6- Agregar las environment para el usuario y contraseña de la base de datos:

> Ingresar en la configuracion de ejecucion.

![image](https://github.com/user-attachments/assets/8420c382-222e-4ba5-9c18-bc119282bbe1)

> Agregar las environment correspondientes: BD_PASSWORD y BD_USERNAME con los datos correspondientes.

![image](https://github.com/user-attachments/assets/1944bf60-cdce-4b38-a1b2-1173c824bb60)

### 7.1- Asegurarse de tener corriendo el servidor de MySQL en los servicios

#### 7.2 Generar la base de datos en el motor de base de datos (Workbench)

```sql
CREATE DATABASE IF NOT EXISTS `server_grpc`;
```

### 8- Ejecutar el proyecto en src/main/java/server/ServerGrpcApplication

![image](https://github.com/user-attachments/assets/cc61c2bf-6ea8-4ef2-9f4e-b2dd9a8de12c)

### 9- Confirmar que el servidor esta funcionando

> Se deberia ejecutar en el puerto 9091 para asegurarse que funciona correctamente.

![image](https://github.com/user-attachments/assets/f006ebf3-93fd-467a-9a30-5c319769d166)

### 10- Asegurarse de que se haya generado la tabla "User" (de prueba) Correctamente.

```sql
USE `server_grpc`;

SHOW TABLES; # VERIFICAR QUE SE HAYA CREADO LA TABLA "user"

INSERT INTO `server_grpc`.`user` (`id`, `name`) VALUES ('1', 'UserTest'); # Test
```
