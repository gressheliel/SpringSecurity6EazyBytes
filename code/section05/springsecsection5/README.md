# 01 Encode Encrypt & Hashing
- Encode
  - No tiene nada que ver con la criptografia
  - Solo realiza una representación de caracteres en otra base
  - Tiene reverso
  - Base64, ASCII, Unicode son opciones
  - Cuando se realiza encripción se pueden codificar los datos en base64 para darles un formato legible
  - Ej. 
    - openssl base64 -in plain.txt -out encode.txt
    - openssl base64 -d  -in encode.txt  -out decode.txt
- Encripción
  - Transformar datos en forma tal que se garantice su confidencialidad
  - Implica secrets, Key private, key public, algoritmo de cifrados
  - Es reversible usando su llave
  - Simétrico. Con la misma llave se encripta y desencripta
  - Asimétrico. Utiliza 2 llaves private, public. Encripta con la publica, desencripta con la privada
  - Ej. 
    - openssl enc -aes-256-cbc -pass pass:12345 -pbkdf2 -in plain.txt -out encrypt.txt -base64  
    - openssl enc -aes-256-cbc -base64 -pass pass:12345 -pbkdf2 -in encrypt.txt -out decrypt.txt 
- Hashing
  - Los datos son convertidos usando funciones hash
  - Si se manda el mismo texto a la misma función hash se obtiene el mismo valor hash
  - No son reversibles (Fruits -> Blender -> Juice)
  - Una función hash recibe una entrada  de cualquier longitud y la transforma en una longitud reducida
    -  Zorro                         -> Hash -> DFCD3454
    -  El zorro rojo                 -> Hash -> 52ED879E
    -  El zorro rojo corre a traves  -> Hash -> 46042841
  - Ej.(openssl, si se ejecuta multiples veces, el resultado es el mismo)
    - echo -n "EazyBytes@12345" | openssl dgs -sha256 decode.txt
      SHA2-256(stdin) = 177b845bf42225b6da04ad14df88e4f27b6d872c3c4143f1f37c22431bb9dc1e

# Mejoras al proceso de hashing. 
## Generar Salt
- Como el hash produce siempre la misma salida, podrían duplicarse los hash al proporcionar la misma contraseña
- Es recomendable utilizar los salt (valores aleatorios)
- Ej.
  - User escribe su pwd=12345 -> se genera salt= aex2fdac -> con: 12345+aex2fdac genera hash -> hash=af4c. Guardar Salt y Hash
  - En el login, por medio del user se recupera el salt, se le concatena al pwd, se genera el hash y valida que sean iguales.

## Bloquear contraseñas
- Permitir 3 intentos fallidos

## Ralentizar el tiempo de los hash
- Las funciones hash son muy agiles, para evitar ataques de fuerza bruta se recomienda ralentizarlos
- bcrypt es una opción

# En spring security <<interface>> PasswordEncoder
- String encode(CharSequence rawPassword); Genera el hash con un salt de 8 bytes
- boolean matches(CharSequence rawPassword, String encodedPassword); Coinciden las contraseñas
- default boolean upgradeEncoding(String encodedPassword) {
  return false;
  }   /*Cuando requiere ejecutar 2 veces el hash en una contraseña.*/
  
  