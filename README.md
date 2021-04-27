# Presentación
## ¿Quién es Sofía?
Sofía es un servidor con soporte para crear rápidamente aplicaciones Web.

La filosofía atrás de Sofía se basa en la idea de que una gran parte de una aplicación Web es común a muchas otras aplicaciones Web y se busca facilitar la reutilización de ese código en común.

Al diseñar el marco de trabajo se tuvo presente la idea crear la menor cantidad de conceptos nuevos posibles. El marco de trabajo permite la reutilización de código Java y HTML5 utilizando muy pocas ideas básicas.

Se trató de reducir al máximo los nuevos conceptos para evitar ampliar la distancia entre las tecnologías existentes y la metodología de trabajo.

Sofía es un servidor web, un marco de trabajo, un conjunto de librerías y una API que permite crear aplicaciones Web utilizando componentes ya existentes que trabajan apoyándose en una API que ofrece una gran cantidad de servicios.

La forma de trabajo de Sofía se basa en la reutilización de código. Cuando creamos nuestras páginas podemos reutilizar plantillas de páginas ya existentes, componentes ya existentes, reutilizar fragmentos de código HTML y crear plantillas o componentes propios.

El Core en el cliente Web se comunica con servicios ya existentes en el backend y permite crear servicios propios o extender la funcionalidad con nuevas características.

### Prerequisitos
Para utilizar Sofía necesitamos tener instalado Java versión 8 o superior, mySQL 8 o MariaDB 10.

## Instalación
### Instalar localmente
Descargue el archivo ZIP del sistema y descomprímalo en la ubicación que desee. El archivo ZIP contiene un JAR para ejecutar el servidor y un directo de datos de la aplicación.

## Configuración
Antes de que el servidor funcione debemos de configurarlo. Sofía utiliza solo un archivo de configuración llamado `sofia.configuration.properties` que debemos de crear en una ubicación específica. El servidor primero busca el archivo de configuración en el directorio donde se está ejecutando y luego lo busca en el home del usuario que se está ejecutando. Si encuentra un archivo con ese nombre en alguna de estas ubicaciones lo toma. Si no lo encuentra muestra un mensaje de error. Las ubicaciones donde busca del archivo de configurarión se muestra en el log al ejecutar el jar.

El archivo de configuración está pensado para contener la mínima configuración necesaria para ejecutar el servidor. La siguiente es un ejemplo de la mínima configuración que se debe de tener. La forma mas directa de configurar el servidor es tomando el siguiente código y copiarlo en un archivo de nombre `sofia.configuration.properties` en una de las rutas mostradas al arrancar el servidor y luego cambiar los valores a los que se desee. Vamos a explicar brevemente de que se trata cada uno de los valores.

```properties
environment=development
server.port=8080
database.driver=com.mysql.cj.jdbc.Driver
database.hostname=127.0.0.1
database.port=3306
database.name=sofia
database.username=juan
database.password=tenorio2017
system.home=/home/juan/servidor
```
`environment` hace referencia al ambiente donde se está ejecutando el servidor. Puede tomar dos valores: `development` o `production`. `development` indica que se está ejecutando el servidor en un ambiente de desarrollo que permite acceder a ciertas características que facilitan el desarrollo. Por ejemplo, se puede indicar que se ha accedido al sistema con determinado usuario desde la url para evitar registrarse para hacer pruebas con determinado usuario. También se genera código mas fácil de leer y debugear, se muestran nombres de archivos origen en el código generado y un sinnúmero de otras facilidades de las cuales hablaremos mas adelante. Cuando se configura como `production` el servidor se centra en la velocidad y elimina información innecesaria del código. `server.port` es un valor entero que indica el puerto donde se va a arrancar el servidor. Si vamos a usarlo localmente, la opción mas común es `8080` ya que no se necesitan privilegios de root para ejecutarlo y nos evitamos problemas de permisos. En producción vamos a querer correrlo en el 80 para que sirva HTTP.

La propiedad `database.driver` indica el driver jdbc que vamos a utilizar para acceder a la base de datos. Está ahí para que se pueda cambiar en el futuro. Actualmente se utiliza solamente mySQL para desarrollar, pero se podría usar otras bases de datos en el futuro. Por lo pronto no es necesario tocar esta propiedad.

El host del servidor de base de datos se indica en `database.hostname`. La mayoría de las veces se puede dejar esta propiedad sin cambiar.

`database.port` permite configurar el puerto en el cual la base de datos está corriendo. Ya que usamos únicamente mySQL o MariaDB no hay necesidad de modificarlo.

`database.name` define el nombre de la base de datos a utilizar. A no ser que ya se esté utilizando una base de datos con el mismo nombre no es necesario modificar esta propiedad.

Las propiedades que seguro hay que modificar son las siguietnes tres.

`database.username` indica el usuario con el cual el sistema va a acceder a la base de datos. Este usuario debe tener los siguientes privilegios: `CREATE`, `DROP`, `INDEX`, `INSERT`, `LOCK TABLES`, `SELECT` y `UPDATE`.

Con `database.password` especificamos la contraseña que se va a utilizar para acceder a la base de datos.

`system.home` indica el directorio donde se va a colocar el directorio de datos de la aplicación. Este viene en el paquete del servidor y se llama `system`. La propiedad `system.home` indica mediante una ruta completa el directorio donde `system` es colocado sin especificar la barra al final.

Una vez que hemos editado el archivo de configuración y estamos seguros de que vamos a utilizar esas propiedades de configuración podemos correr el servidor.

## Ejecutando el servidor

Para ejecutar el servidor usamos el siguiente comando:
```
java -cp target/sofia-web-server-1.0.jar:./system/data/libs/ net.cabezudo.sofia.core.WebServer
```
Si queremos que borre la base de datos podemos usar:
```
java -cp target/sofia-web-server-1.0.jar:./system/data/libs/ net.cabezudo.sofia.core.WebServer -dd -i
```
Y si además queremos que trabaje en modo silencioso, esto es, que no pregunte por dominio o usuario administrador y configure con el usuario por defecto podemos colocar el modificador `--ide` o simplemente `-i':
```
java -cp target/sofia-web-server-1.0.jar:./system/data/libs/ net.cabezudo.sofia.core.WebServer -dd -i
```
Si necesitamos datos para pruebas podemos agregarlos a nuestra librería y ejecutar con un modificador para crearlos
```
java -cp target/sofia-web-server-1.0.jar:./system/data/libs/ net.cabezudo.sofia.core.WebServer -dd -i -ctd
```
Es importante tener presente que se necesitan permisos de root para ejecutar en puertos por abajo de 1024. El servidor no va a indicar que no tiene permisos para ejecutarse, solo va a quedar esperando indefinidamente.

En este punto, luego de configurado, el servidor debe mostrar la ruta donde encuentra el archivo de configuración y la configuración leida. Luego va a crear la base de datos y las tablas y la configuración por defecto.

En este punto, el servidor nos va a solicitar los datos para la cuenta administrador del servidor. Debemos colocar el nombre y apellido del administrador, la dirección de correo y la contraseña. Cuando terminemos de colocar estos datos, se continua creando valores por defecto en la base de datos y se termina el proceso de configuración indicando que el servidor ha sido arrancado y a la espera de peticiones.

### Problemas al arrancar
Pueden existir algunos problemas si, por ejemplo, detenemos la ejecuación en la mitad de la configuración. Si esto sucede pueden existir una inconsistencia en los datos por defecto generados o simplemente no existen todos los datos necesarios para ejecutar. Si esto sucede podemos borrar la base de datos desde un cliente de mySQL. Pero podemos solucionar este tipo de problemas indicandole al servidor que ejecute ciertas tareas desde línea de comandos.

## Opciones de línea de comandos
En los casos que la base de datos quede en un estado inconsistente o que nos hayamos equivocado al colocar el usuario administrador podemos ejecutar operaciones directamente desde la linea de comandos. Una de las operaciones podría ser borrar la base de datos.

Intente arrancar el servidor agregando la opcion `--help`
```
$ java -jar sofia.cabezudo.net.jar --help
Sofia 0.1 (http://sofia.systems)
-h, --help - This help.
-d, --debug - Print all the debug information.
-cr, --configureRoot - Configure the root information.
-dd, --dropDatabase - Drop de database and create a new one.
-i, --ide - Configure the system to work inside an IDE
```
La opción `--help` muestra todas las opciones actuales del servidor y termina, para cuando nos olvidemos de que podemos hacer en la línea de comandos.

La opcion `--debug` indica que aumente la información mostrada en consola. Hay pequeños detalles que nos pueden ayudar a resolver problemas.

Si queremos modificar el usuario administrador podemos usar `--configureRoot`, que permite modificar la información del usuario administrador sin tener que borrar la base de datos.

Si queremos borrar toda la base de datos para que vuelva a recrearla podemos utilizar la opción `dropDatabase` que directamente borra la base de datos definida en la configuración.

La opcion `--ide`, le indica al servidor que vamos a trabajar dentro de un IDE y modifica su comportamiento para facilitar la tarea de desarrollar. Dentro de un IDE podemos indicar que queremos que siempre se borre la base de datos mediante constantes o crear automáticamente el administrador para no tener que configurarlo cada vez que borremos la base de datos. Dentro de un IDE el log mostrado es el mas detallado.

### Instalación en Debian
La siguiente es una guía para instalar sobre un sistema Linux con la distribución Debian o Ubuntu. La guía está pensada para instalar el servidor en un sistema que no tenga instalado ni mySQL ni Java.

Primero vamos a instalar Java. Para esto vamos a actualizar los paquetes y vamos a instalar la versión abierta de Java.
```
sudo apt-get update && sudo apt-get upgrade
sudo apt-get update
sudo apt-get install openjdk-8-jdk
```
Vamos a verificar que la versión de Java sea la correcta para asegurarnos de que instaló correctamente.
```
java -version
```
Luego vamos a instalar mySQL
```
sudo apt-get install mysql-server
```
Utilizando el cliente del servidor mySQL recién instalado vamos a crear el usuario para que Sofía pueda accedera a la base de datos.
```sql
CREATE USER 'sofia'@'localhost' IDENTIFIED BY 'databasUserPassword';
GRANT ALL PRIVILEGES ON sofia.* TO 'sofia'@'localhost';
FLUSH PRIVILEGES;
```
Luego vamos a crear el archivo para servicio que va a arrancar Sofía cada vez que se reinicio el servidor.
```
$ sudo nano /etc/systemd/system/sofia.service
```
Dentro del archivo de servicio vamos a copiar el siguiente text:
```
[Unit]
Description=Sofia server

[Service]
User=esteban
# The configuration file application.properties should be here:
# change this to your workspace
WorkingDirectory=/home/esteban/sofia
# path to executable.
# executable is a bash script which calls jar file
ExecStart=/home/esteban/sofia/run.server.sh
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```
Luego vamos a crear un directorio para colocar los archivos del servidor.
``` bash
$ mkdir /home/esteban/sofia
$ nano /home/esteban/sofia/run.server.sh
```
Vamos a copiar en el archivo el siguiente contenido:
``` bash
#!/bin/sh
sudo java -jar dist/sofia.cabezudo.net.jar
```
No olvidemos agregar los permisos de ejecución para el archivo.
``` bash
$ chmod u+x /home/esteban/sofia/run.server.sh
```
El siguiente paso en bajar el paquete de Sofía. Vamos a cambiarnos de directorio para que sea mas fácil la descarga y la descompresión. Bajamos el paquete con `wget` y luego lo descomprimimos con `unzip`. Borramos el paquete para que no haga ruido.
``` bash
$ cd /home/esteban/sofia/
$ wget https://github.com/Cabezudo/Sofia/raw/master/sofia.dist.zip
$ unzip sofia.dist.zip
$ rm sofia.dist.zip
```
Luego vamos a crear el archivo de configuración de Sofía. Los mas directo es crearlo en el directorio donde se va a ejecutar el servidor.
```bash
$ nano sofia.configuration.properties
```
Vamos a copiar las siguientes líneas dentro del archivo. Como ya mensionamos hay que colocar los valores que se hayan definido para el nombre de usuario y la contraseña de base de datos.
```properties
environment=production
server.port=80
database.driver=com.mysql.cj.jdbc.Driver
database.hostname=127.0.0.1
database.port=3306
database.name=sofia
database.username=sofia
database.password=password
system.home=/home/esteban/sofia
```
Ahora vamos a poder arrancar nuestro servidor utilizando
```bash
$ sudo systemctl start sofia.service
```
Dejamos una guía para tener a mano de las opciones que tenemos con `systemctl`.
```bash
$ sudo systemctl daemon-reload
$ sudo systemctl enable sofia.service
$ sudo systemctl stop sofia.service
$ sudo systemctl status sofia.service
$ sudo systemctl
```
Por último, si queremos ver lo que el servidor está registrando podemos utilizar:
```bash
$ sudo tail -f /var/log/syslog
```
## El servidor
Sofía es un servidor de aplicaciones a la cual se le puede agregar sitios web, en el contexto del servidor los llamamos sitios. Cada sitio puede responder a uno o mas nombres de dominios o subdominios. El servidor está pensado para correr muchos aplicaciones en dominios diferentes de forma rápida y haciendo economía de recursos. Cuando se instala el servidor automáticamente se crean dos espacios de trabajo. El primero es la administración del servidor. Podemos ver la administración escribiendo en la URL de un navegador lo siguiente:
```
http://localhost:8080/
```
Por defecto tambien se asocia el administrador del servidor como `manager`. Esto quiere decir que si asignamos `manager` a 127.0.0.1 en el archivo de `hosts` podemos entrar a la consola de administración usando:
```
http://manager:8080/
```
En entornos Linux podemos configurar esto en `/etc/hosts` para agregar manager. El archivo de hosts quedaría parecido a este:
```
127.0.0.1	localhost
127.0.0.1	manager

# The following lines are desirable for IPv6 capable hosts
::1     localhost ip6-localhost ip6-loopback
#milky.anemonagroup.com
ff02::1 ip6-allnodes
ff02::2 ip6-allrouters
```
Sobre macOS podemos modificar el archivo `/private/etc/hosts`. Debería de quedar de la siguiente forma:
```
##
# Host Database
#
# localhost is used to configure the loopback interface
# when the system is booting. Do not change this entry.
##
127.0.0.1 localhost
255.255.255.255 broadcasthost
::1 localhost
127.0.0.1 manager
```
Ya que modificamos el archivo de host podríamos agregar también `playground` ya que cuando el servidor crea los datos básicos para funcionar también crea un sitio llamado Playground. Este es utilizado para hacer pruebas y es el que vamos a utilizar para todos los ejemplos.

## Crear aplicaciones
### Espacios de trabajo y directorios
Cada sitio tiene su propio espacio de trabajo y es en este espacio donde vamos a crear nuestras aplicaciones. El sistema tiene un directorio que configuramos en nuestro archivo de configuración con el nombre de `system.home`. Dentro de ese directorio se encuentra un directorio `system` donde se encuentra un directorio `sources` donde están todos los fuentes de todos los sitios y las librerías. El directorio donde se encuentran los fuentes de los sitios se llama `sites` y dentro de este se encuentran todos los directorios de los sitios existentes en el servidor. Si el sitio es totalmente nuevo solo vamos a tener `manager` y `playground`. Pero cuando creemos nuevos sitios van a comenzar a aparecer otros directorios con el nombre del host principal de nuestro sitio. Mas adelante vamos ver de que se trata esto al crear nuevos sitios.

### Versionamiento
Vamos a notar que dentro de cada directorio hay un directorio con un número uno. Ese número uno es la versión del sitio. Cada versión para un sitio se encuentra en un directorio. Para crear una nueva versión solo tenemos que copiar el directorio anterior y agregar lo que queramos. La versión del sitio que se va a correr se puede especificar en la configuración del sitio.
Dentro del directorio de versión encontramos los fuentes para esa versión y es donde vamos a comenzar a desarrollar nuestra aplicación.

### Construyendo una página
Para crear una página Sofía toma todos los elementos involucrados en la página y genera, sin contar imágenes, videos y fuentes, tres archivos. Uno para HTML, uno para JavaScript y otro para los estilos. De esta forma solo son tres los archivos que se transfieren reduciendo el tiempo de carga de la página.

Los archivos creados son almacenados en una ubicación específica que podemos consultar para depurar nuestras aplicaciones. En el mismo directorio donde se encuentran el directorio `sources` existe un directorio llamado `sites`. En este directorio se crean los directorios que contienen los sitios creados. Cada sitio tiene un direcotrio con el nombre del host principal del sitio. Dentro de este se encuentra otro directorio que se llama con el numero de versión. Dentro de este los archivos generados

Entre los archivos generados vamos a encontrar un directorio `css` para las hojas de estilo, un directorio `js` para el código JavaScript y todos los archivos HTML generados. Debemos notar que por cada arvhico HTML hay un archivo CSS con el mismo nombre y un archivo de JavaScript con el mismo nombre. De esta forma podemos saber que archivo corresponde a que HTML. Debemos notar también que si se colocan archivos dentro de directorios, la estructura de directorio se crea de igual forma. Podemos, para ver esta estructura, navegar por los archivos generados para `manager`.

Vamos a crear una página para comenzar nuestro sitio. En el patio de juegos ya existe un archivo llamado `index.html`. Si colocamos en la URL de un navegador `http://playground/` y todo está bien configurado vamos a poder ver una página en blanco con un mensaje. "Bienvenido al patio de juegos de tus ideas".

Cuando Sofía crea un sitio utiliza páginas principales y fragmentos. La forma de distinguir una página principal de un framento es mediante la primer línea. Si la primera línea comienza con `<!DOCTYPE html>` estamos frente a una página principal.

Solamente se van a crear páginas que tengan esa línea al inicio y se van a convertir en páginas del sitio que se pueden referir como un recurso de la apliación. Esto quiere decir que si marcamos una página `test.html` como principal vamos a poder acceder a ella utilizando `http://playground/test.html`, de otra forma va a ser un fragmento y no vamos a poder acceder a ella desde el navegador.

Los fragmentos entonces, son utilizados cuando hay código HTML que se repite en varias de nuestras páginas. El encabezado, el pie de página o inclusive componentes pueden ser creados como fragmentos. La diferencia entre un componente y un fragmento es que los componentes son mas genéricos y pueden ser insertados varias veces en una página con diferentes configuraciones. Un fragmento pertenece al sitio donde se encuentra y a nadie mas, en cambio un componente puede ser utilizado por cualquier sitio dentro del servidor.

Para acelerar el proceso de desarrollo, cuando el servidor está funcionando en un ambiente de desarrollo, siempre se crean los archivos al recibir una solicitud del navegador. Cuando el ambiente es de producción, esto no sucede, lo que permite que el ambiente sea mas rápido. En producción solo son creados cuando se fuerza una creación, o cuando el sitio es creado.

### Estructura de una página principal
Si bien hay algunas diferencias entre una página principal de Sofía y una pagina HTML, estas son mínimas. Se trató de introducir la menor cantidad de conceptos nuevos posibles para evitar tener que considerar mas cosas de las que se consideran cuando se programa únicamente HTML5.

La primer diferencia con una página HTML es el atributo `profiles` en la etiqueta `<html>`. Este atributo define que perfiles pueden acceder a la página. En el caso del patio de juegos, e inclusive de esta página, este atributo tiene el valor `all`. Esto quiere decir que todos los perfiles pueden acceder a la página, inclusive cuando no existe perfil como cuando no hay ningún usuario registrado en el sistema. En este caso tampoco hay un perfil asociado.

La segunda diferencia no es exactamente una diferencia. Es algo que debemos hacer de forma obligatoria para que todo funcione correctamente. Supongamos que tenemos una página llamada `test.html` en el directorio raíz de nuestra aplicación. En ese caso estamos obligados a colocar estas líneas en la página principal:
```html
<link rel="stylesheet" type="text/css" href="/css/test.css">
<script src="/js/test.js"></script>
```
Estas líneas incluyen los archivos de estilos y de JavaScript necesarios para funcionar. Deben tener el mismo nombre que el archivo HTML que se está escribiendo. Si el archivo HTML está en una ruta en particular hay que incluir la ruta para que pueda encontrar los archivos generados.

Otra diferencia con un archivo de HTML5 es que podemos colocar dentro de la etiqueta `<head>` una línea como la siguiente:
```<style file="documentation.css"></style>```
No importan si tiene espacios en blanco adelante y atrás de la línea, lo único a tener en cuenta es que debe ser lo único escrito en esa línea. Esta línea permite especificar un archivo de estilos que será agregado a el archivo de estilos generado. No es posible agregar de manera indiscriminada archivos de estilos como si fuera HTML normal, si se agregan solo serán generados los archivos anteriormente mencionados y cualquier otro archivos que se intente colocar será ignorado en el momento de creación.

Otra diferencia son dos atributos en la etiqueta `<section>`. Esta etiqueta ahora admite tres atributos: `file`, `template` y `configurationFile`.

Si le colocamos el atributo `file` el contenido de la etiqueta será sustituido por el contenido del fichero especificado. De esta forma incluimos fragmentos de archivos HTML que se encuentran en la estructura de directorios de nuestros fuentes, dentro de nuestro código.

Si especificamos el atributo `template` el contenido de la etiqueta será sustituido por una plantilla de componente que se encuentre dentro de las librerías. Esta es la forma que tiene el sistema de reutilizar componentes que ya hechos.

El atributo `configurationFile` se utiliza solo dentro de la etiqueta `section` y es utilizado para evitar que se intente utilizar el archivo de variables de plantilla por defecto y especificar la ubicación de un archivo de  variables de plantilla en particular. Esto puede ser utilizado para evitar repetir archivos de variables de plantillas.

Si se agrega contenido a la etiqueta o se escribe en varias líneas o se agrega otro atributo la etiqueta será tratada como una etiqueta normal. El siguiente código muestra ejemplos de uso de la etiqueta.
```html
<section file="login/loginForm.html"></section>
<section template="logins/basic-login/login.html"></section>
```
La primer línea lee un archivo, la segunda carga un componente.
Por otro lado los siguientes ejemplos se van a comportar como si fueran código HTML normal.
```html
<section id="menu"></section>
<section>Este es un ejemplo de sección HTML</section>
<section class="listaDeNombres"></section>
```
### Estructura de un fragmento de página
Un fragmento es mucho más simple que una página HTML. Es apenas un poco mas del código que quedaría si tomaramos el contenido de una etiqueta HTML y lo colcaramos en un archivo. El siguiente es un ejemplo muy simple de fragmento.
```html
<div id="foot">
  <div class="copy">© 2019 Cabezudo. All rights reserved.v/div>
  </div>
```
Es exactamente el contenido de una etiqueta cualquiera. La diferencia es que podemos colocar dos etiquetas mas dentro del archivo.

Para organizar el código de HTML5 en Sofía tratamos de agrupar todo el código que tiene relación entre si en un solo lugar. Es por eso que un fragmento puede tener dos etiquetas de una página HTML normal. Estas son `<script>` y `<style>.`. Estas deben de ir solas en una línea. La de apertura y la de cierre. Todo lo que se encuentre dentro de estas etiquetas serán movidos a sus respectivos archivos de estilos y JavaScript.

El uso de la etiqueta de JavaScript es el mas fácil de explicar. Tenemos un componente, queremos iniciarlo, agregarle disparadores, estilos, crearlo a partir de un archivo de configuración o para lo que se nos ocurra que podemos aplicarle JavaScript. Para esto, usamos la sección `<script>`.

La sección `<style>` es para lo mismo. Definir un estilo en particular para un componente en particular. El siguiente es un ejemplo de fragmento de código con toda su funcionalidad.
```html
  <style>
    #foot {
    border-top: 1px solid lightgray;
  }
  #foot > div.content {
    width: #{foot.container.width};
    margin: auto;
    border-bottom: 1px solid lightgray;
    display: flex;
  }
  </style>
  <script>
    const createGUI = () => {
      // Crear algo de la interfaz
    };
    Core.addOnloadFunction(createGUI);
  </script>
  <div id="foot">
    <div class="copy">© 2019 Cabezudo. All rights reserved.</div>
  </div>
```
No importa si colocamos una sección antes de la otra o inclusive si colocamos varias. Serán agregadas al archivo final en el orden que se encuentran en el archivo fuente.

### ¡Hola mundo!
Vamos a crear nuestra primer página. Para esto vamos a usar el patio de juegos. Como no queremos perder la página principal del patio de juegos vamos a copiarla y a crear una nuevo que vamos a llamar `test.html`. Vamos al sistema de ficheros y copiamos `index.html` y le ponemos el nombre `test.html`. Ahora tenemos una pagina nueva con la estructura mínima para una página principal.

Vamos a cambiar la línea 7 y vamos a poner `test.css` en lugar de `index.css` y vamos a cambiar la línea 8 para que diga `test.js` en lugar de `index.js`.

Vamos a borrar la línea 30 y a borrar el contenido de la etiqueta `style`.

Ahora está lista nuestra completamente nueva página. El siguiente paso es colocar un texto centrado en nuestra pantalla que diga "¡Hola mundo!".

Podríamos haber dejado el código anterior ya que este mostraba un texto centrado en la pantalla, pero no iba a servir para nuestros propósitos ya que vamos a utilizar un componente prehecho que centra los textos dentro de una etiqueta HTML.

Para hacer esto vamos a preparar nuestra aplicación para que utilice la pantalla completa como espacio para un componente. El siguiente estilo hará que se use toda la pantalla aun cuando no haya contenido. Además, queremos que los diferentes componentes se coloquen uno encima del otro como una pila. Por eso usamos la dirección `column`
```html
#application {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
```
Una vez que está preparado el espacio para la aplicación vamos a agregar dentro de sección de la apliación la siguiente línea:
```html
  <section id="helloWord" template="text/centered/index.html"></section>
```
Esta línea indica que hay que agregar una sección que utilice el template `text/centered/index.html` en ese lugar. Ahora podemos colocar en la entrada de URL de nuestro navegador `http://playground/test.html` y ver el texto centrado.

Vemos un texto centrado pero no es el texto que queremos. Necesitamos colocar nuestro propio texto en ese lugar. Para esto tenemos que configurar el componente para colocar el texto que vamos a usar. Por lo general los componentes tienen un archivo de configuración, este tiene el mismo nombre que el archivo HTML del componente pero con extensión `json`. En ese archivo podemos ver las opciones de configuración de nuestro componente. Vamos a buscar el archivo para nuestro texto centrado. Si abrimos el archivo vemos que tiene muchas propiedades. La que nos interesa es la propiedad llamada `text`. Esa propiedad define el texto que se va a mostrar en pantalla. Vamos a definir entonces esa propieada para nuestro componente. No vamos a cambiarla ahí porque estaríamos cambiando el valor por defecto para todas las aplicaciones que usen ese componente. En lugar de eso vamos a agregar la propiedad en el archivo de configuración de nuestra página `test.html`.

A todas las páginas principales se les puede colocar un archivo con propiedades para los componentes que está utilizando. En el caso de nuestra página `test.html` el archivo de configuración se va a llamar `test.json`. Vamos a crear este archivo y vamos a escribir la configuración para nuestro componente.
```json
{
  "helloWord": {
    "text": "¡Hola mundo!"
  }
}
```
Cada etiqueta `section` que hace referencia a un componente tiene un id. Este id sirve, no solamente para hacer referencia al componente en la página, también es utilizado para indicar que configuración corresponde a que componente dentro del archivo de configuración de nuestra página principal. En este caso el id de nuestro componente es `helloWord`, por lo tanto vamos a agregar una propiedad con ese nombre para colocar la configuración que le corresponde a ese componente como un objeto valor.

Solo vamos a colocar las propiedades que nos interesan cambiar. En este caso, solo el texto. Pero podemos jugar con los otros valores para experimentar un poco.

Esto parece muy simple. Podríamos hablerlo escrito en 5 minutos y sin tanto problema. Pero imaginemos algo mas complicado. Imaginemos que tenemos que colocar varios textos centrados en una misma página. Imaginemos ahora algo mas complejo como un menú o un acceso a usuarios registrados. Utilizando esta idea, para agregar un acceso a usuarios a nuestro sitio solo debemos cambiar la propiedad template de la linea a la cual apunta nuestro componente y poner la siguiente línea.
```html
  <section id="login" template="logins/basic-login/login"></section>
```
Si. Un login completo, totalmente funcional, que permite a una persona registrarse en el sistema, que valida en tiempo real si el correo está bien formado o si el nombre de dominio es válido. Todo sin hacer absolutamente nada mas.

### Estilos
### Javascript
### Core
### Variables en común para todo el sitio
Las variables colocadas en el archivo `commons.json` pueden ser utilizadas en los archivos de configuración de fragmentos y componentes.
### Variables de plantillas
Las variables de plantilla son variables que indican valores que se le van a aplicar a las plantillas. Simplemente son archivos JSON con valores colocados de cierta forma.
// TODO continuar con esta sección

### Orden de asignación de variables
Las variables de plantillas pueden ser colocadas en el archivo `commons.json`, en el archivo de configuración por defecto del componente dentro de las librearías o en el archivo de configuración para el componente el el fuente del sitio. Si definimos una variable de configuración para un componente en el archivo `commons.json`, esta, es sobrescrita por la variable en el archivo de configuración por defecto. Es por esto que no podemos definir una variable de plantilla en `commons.json`. Debemos definirla en un archivo para la página que contiene el componente. Primero se leen las variables de plantilla de `commons.json`, luego se leen las variables de plantilla por defecto del componente y luego las variables de plantilla definidas para ese componente en el archivo de configuración de la página. Cada una de estas definiciones sobrescribe la definición anterior.

### Páginas
### Librerías
Las librerías contienen funciones de JavaScript. Estas pueden ser cargadas en la página y reutilizadas en cualquier lugar.

No se puede especificar la misma librería con dos versiones diferentes en una misma pagina. Actualmente las dependencias de los componentes y las librerías definen la versión a utilizar.

### Componentes
### Javascript
### Variables del sistema
#### URLSearchParams
### Estilos
Las fuentes de todo el sitio se definen en el archivo de estilos fonts.

Hay varios niveles con los cuales se definen los estilos del sitio. La forma mas simple es definir el estilo en el archivo que lo utiliza. Si hacemos una buena elección de nombres no deberíamos de tener conflictos.

La definición de estilos es un poco mas complejo que eso. Por defecto se define en `commons.json` un tema para el sitio utilizando la propiedad `themeName`. El nombre del tema especifica un directorio dentro de `themes` que se encuentra dentro del directorio `data`. Dentro de ese directorio vamos a encontrar un archivo `style.css`que define los estilos del tema y un archivo `values.json` que define los valores que se pueden modificar para ese tema. Si se planea crear nuevos temas se espera que estas propiedades definidas aquí permanezcan de la misma forma para intercambiar temas sin modificar código.

El tema define los estilos para todos los componentes que están definidos para HTML y se definen variables de plantilla para valores globales del sitio. El tema define valores para la fuente del texto y de los títulos, su tamaño, colores principales y otros valores que se espera compartan entre temas para poder cambiar el tema sin temor a que se defina erróneamente algún estilo del sitio. Esto debe tenerse en cuenta a la hora de escribir nuevos temas e inclusive componentes que se quieran compartir.

Los valores para los estilos se definen inicialmente en el archivo de valores para el tema. Luego se toman los valores definidos en el archivo `commons.json`. Estos valores serán mezclados y sobreescritos en el siguiente orden: variables de plantillas por defecto para la página, variables de plantillas definidas por el usuario para la página. Los componentes no sobreescriben estas variables porque el identificador del componente es agregado a la variable de componente para poder definir distintos valores para un mismo componente colocado dos veces en la misma página.

Una variable de plantilla definida en `commons.json` puede ser utilizada en un template para definir un valor de la misma forma en que se usa en un archivo HTML.

Para encontrar el lugar donde se lanzó un error de literal no definido (undefined literal) tenemos que buscar primero en el componente indicado por el error. Si no hay una llamada a ese literal en el componente debemos ver el nombre hasta el primer punto. Este es el id del componente llamado. Si buscamos dentro de ese componente vamos a encontrar la llamada al literal o repetimos. Buscamos un componente con el identificador de la cadena hasta el siguiente punto y repetimos la búsqueda.

También podemos definir variables de plantilla usando el id del template que vamos a agregar a la página. De esta forma, si varias páginas van a utilizar un mismo componente se puede compartir la configuración. Un ejemplo común es un menú. Podemos utilizar un componente para el menú y colocar el mismo id para ese menú en todas las páginas en donde se quiere compartir la configuración. Este id se utiliza para nombrar el archivo de configuración del menú que se comparte entre todos. El archivo de variables de plantilla para la página sobrescribe las variables definidas en el archivo de variables de plantillas para el id.

## Core
### Funciones
#### addMessage: message
#### addOnloadFunction: func
#### changeSection: section
#### cleanMessagesContainer
#### getNextRequestId
#### getRequestId
#### getURLParameterByName: (name, url)
#### hide: id
#### isEnter: event
#### isFunction: v
#### isLogged
#### isModifierKey: event
#### isNavigationKey: event
#### isNotLogged
#### isRightLeft: event
#### isRightClick: event
#### isTouchStart: event
#### isVisible: element
#### removeChilds: element
#### screenBlocker
##### create
##### block
##### unblock: options
#### sendGet: (url, origin)
#### sendPost: (url, origin, formObject)
#### sendPut: (url, origin, formObject)
#### setMessagesContainer: target
#### show: id
#### trigger: (target, eventName, detail)
#### validateById: id
#### validateElement: element
#### validateIdOrElement: (id, element)
### window.onload

## Usuarios y privilegios de acceso
Si el servidor está trabajando en un ambiente de desarrollo se puede colocar en la url un parámetro ``user`` con la dirección de correo de un usuario válido. Automáticamente el sitema accede con ese usuario y sus privilegios. Esto permite que se prueben características que solo pueden ser probadas con un usuario registrado en el sistema sin necesidad de realizar el proceso de acceso al sitema cada vez que se realiza la prueba.

# El servidor
## Administración
## Usuarios
## Perfiles
## Permisos

## Orden de lectura de archivos
Cuando el sistema crea una página estática toma una serie de archivos para hacer la tarea.

Lee el archivo commons.json y toma los valores comunes para todo el sitio. Toma el el nombre del tema para el sitio. El tema lo va a buscar de un direcotrio `themes` dentro de `data`.

Lee el archivo `fonts.css` que se encuentra en el raíz de los fuentes del sitio y lo agrega a los estilos del sitio. Este archivo se utiliza para colocar en un solo sitio todas las fuentes.

Lee el archivo `style.css` que se encuentra en el raíz de los fuentes del sitio. Este archivo se utiliza para colocar configuraciónes de estilos que no pueden ser colocadas en otro lugar. Configuraciones temporales, de prueba o globales que no pertenecen al tema.

Lee el archivo `style.css` que existe dentro del directorio del tema elegido.

Luego se copia el directorio de imágenes completo de las fuentes al directorio donde se creará el sitio.

Utilizando el nombre de la página a mostrar se busca un archivo de configuración con el mismo nombre. Si la página es index.html se busca primero `index.json`. Si se encuentra el archivo de configuración se agrega las variables de plantilla a las variables de plantilla y se busca una propiedad `template`. Si esta propiedad se encuentra se busca primero un attributo para indicar la configuración a leer, si no se encuentra especificada una configuración en un atributo se busca un archivo con el nombre del id de la sección y extensión `.json`, luego se busca un archivo de configuración con el nombre del archivo plantilla y por último el archivo la plantilla indicada en la propiedad. Si no se encuentra se carga el archivo `index.html`.

Si no se encuentra el archivo de configuración simplemente se carga el archivo `index.html`.

## Funciones de carga de página

para crear GUI


## Funciones de configuración de página
para setear valores por defecto


Se puede colocar un mensaje que será mostrado en la siguiente página que se cargue usando `Core.setSessionMessage(message)`

Las imágenes se colocan en un directorio `images` en el directorio de base de la versión del sitio.

Las variables del query string de la urls se puedeon obtener utilizando Core.queryParameters

Todos los nombres de dominio agregados tienen por defecto un subdominio local que esta pensado para configurar el archivo de hosts para que apunte a la máquina y poder hacer pruebas locales con el servidor.

Todos los nombres de dominio que comienzan con api, por ejemplo api.x.com, apuntan por defecto a x.com/api.

Todos los nombres de dominio que comienzan con admin, por ejemplo admin.x.com, apuntan por defecto a x.com/admin.

## Internacionalización
Sofía tiene soporte para internacionalización incluida entre sus características. Tanto el servidor como las aplicaciones ya manejan todos los lenguajes que se desee.
Para hacer esto se utilizan archivos JSON con el contenido de los textos. LA forma de estos archivos facilita la traducción de mensajes a otros idiomas. Cada texto es una entrada en un objeto cuyas propiedades son claves para acceder al texto correspondiente. Esta proiedad es un objeto cuyas propiedades son los códigos de lenguajes de dos letras que contiene la correspondiente traducción.

```
"longitude.ok": {
    "en": "The longitude value es correct",
    "es": "El valor para la longitud es correcto"
  }
```

Si se desea agregar otro lenguaje simplemente se agrega la propiedad correspondiente.

```
"longitude.ok": {
    "en": "The longitude value es correct",
    "es": "El valor para la longitud es correcto",
    "zh": "长度值正确"
  }
```

Esto es lo único que se necesita para agregar un lenguaje. Cuando se intenta utilizar un lenguaje que no se encuentra en los textos, siempre y cuando la clave exista, el sistema intenta utilizar inglés como reemplazo. Si no es posible utilizar inglés utiliza el primer lenguaje definido. Si no hay lenguajes definido regresa la clave utilizada para hacer la llamada. Esto quiere decir que siempre se obtiene una respuesta de una clave existente en la lista.

La internacionalización se divide en dos partes. El backend y el frontend. Se realiza mediante archivos JSON que contiene todos los textos y los lenguajes existentes.

### Para agregar textos en back

Los textos para el backend de core, esto es, los textos que se muestran en los mensajes de error y en las respuestas de servicios web del servidor se colocan en `src/main/resources/texts.json`. Se deberá de modificar esto si se desea agregar un lenguaje inexistente para el backend.

Dentro del código utilizamos `TextManager`, un singleton que administra los textos del sistema.

Los textos que vamos a utilizar en módulos agregados al servidor, el JAR debe tener un archivo `/META-INF/texts.json` con el mismo formato utilizado en el backend. Para evitar conflictos desagradables a la hora de definir las claves vamos a utilizar el mismo formato que los paquetes de Java, esto es, el nombre de dominio del desarrollador invirtiendo las partes.

```
"net.cabezudo.article.name.empty": {
  "en": "The article name can't be empty",
  "es": "En nombre del artículo no puede estar vacío"
}
```

### Para agregar textos en el front

Existen varias formas para agregar textos en el frontend.

1. Podemos agregarlo agergarlo en el archivo texts.json del raíz de los fuentes del sitio.

1. Usar un archivo `[nombrePagina].texts.json`. Este archivo reemplaza las entradas que encuentre en texts.json

1. Usando la función `Core.addTexts` dentro del código JS. Esto solo funciona para el lenguaje actual por lo que solo se debería de utilizar en una función en tiempo de ejecución. Es una punto de entrada al mecanismo de manejo de textos para aumentar la flexibilidad.

Si desamos poner a cero el servidor en un ambiente de desarrollo podemos utilizar el siguiente commando:
```
java -cp target/sofia-web-server-1.0.jar:./system/resources/libs/ net.cabezudo.sofia.core.WebServer -dd -ctd -d -i
```
De esta forma se recrea la base de datos de cero y se cargan datos de prueba.

### Configuración global del servidor

## Configuración del sitio

El lenguaje por defecto es el aceptado por el navegador y si no se encuentra inglés.