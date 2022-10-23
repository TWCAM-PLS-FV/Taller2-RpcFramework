# RPC Framework

## Autor 📋
**Felipe Valencia** - fevabac@alumni.uv.es


## Implementación 🔧

Los metodos que se nombran a continuación son los que se han usado para dar solución al ejercicio

### __*Clase Utils*__

Una clase llamada Utils, dentro del paquete *java.edu.uv.cs.twcam.pls.rpc* donde creé un método que me permite obtejer el tipo de parámetro, excepción, etc., unicamente con tu tipo y nombre de argumento (de ser el caso).

~~~
public String getType(String arg){
    int i=arg.lastIndexOf(".");
    if(i!=-1){
        String st= arg.substring(i+1, arg.length());
        return st;
    }
    else{
    return arg;
    }
}
~~~

Sin la función anterior, por ejemplo, para el primer parámetro del método "putVM" obtendriamos:

~~~
java.lang.String arg0
~~~

Mientras que, con la función obtenemos:

~~~
String arg0
~~~

### __*Método StartClassStub*__

~~~
- Obtengo el nombre de la interfaz para establecerlo como nombre de la clase y constructor
- Declaro los imports 
- Declaro el constructor e inicializo las variables **host** y **port** con los válores que reciben por parámetro
~~~

### __*Método GenerateMethodStub*__

Obtengo todos los valores que requiero a partir del objeto de tipo Method recibido por parámetro. Desde atributos esenciales como el nombre, tipo de método y parámetros, y un atributo que utilizo como condicional para saber si requiere o no de capturar algún tipo de excepción específica.  

~~~
String methodName = m.getName();
String methodType = m.getGenericReturnType().getTypeName();
Parameter parameters[] = m.getParameters();
Class<?> classObject = m.getReturnType();
Class<?>[] classException = m.getExceptionTypes();
~~~

Creo el método con sus argumentos y tipos

~~~
pw.println("@Override");
pw.print("public " + methodType + " " + methodName + "(");
int index = 0;
for (Parameter p : parameters) {
    pw.print(util.getType(p.toString()));
    index++;
    if (index != parameters.length) {
        pw.print(", ");
    }
}
~~~

Validación para saber si requiere de algún import diferente al establecido por defecto. Con la variable *hasException* controlo si requiere o no del import, en caso *"true"* con la variable *typeException* especifico cuál es la excepción.

~~~
boolean hasException = false;
String typeException = "";
if (classException != null) {
    for (Class<?> exception : classException) {
        hasException = true;
        typeException = exception.getName().toString();
    }
}
~~~

Posteriormente, se crea el bloque *try-catch* donde se inicializa el *Socket*, *ObjectOutputStream* y, en caso de ser necesario, *ObjectInputStream*. En dicho bloque se implementa las funcionalidades para poder establecer/obtener los parámetros requeridos por el servidor.

### __*Método closeClassStub*__

Método donde se realiza un *flush* y se cierra el *PrintWriter*.

## Ejecución 🛠️

Para la ejecución del *CodeGenerator* se siguen los mismos pasos establecidos en la guía de esta actividad.

## Pruebas ⚙️

Previamente instalado el componente *CodeGenerator* en el directorio *".m2"* generamos los archivos

![](/resources/1.PNG)

Lanzamos el servidor

![](/resources/2.PNG)

Lanzamos el cliente con la petición

![](/resources/3.PNG)

Verificamos la petición en el servidor

![](/resources/4.PNG)