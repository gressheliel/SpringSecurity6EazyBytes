# Filtros Personalizados en Spring Security
- Se interceptan TODOS los request que llegan a la App
- Agregar configuracion
- Classes importantes : FilterChainProxy

```
@SpringBootApplication
@EnableWebSecurity(debug = true)
public class EazyBankBackendApplication{...}
```
- En el log se muestra
- Asegurarse que este en TRACE
  - logging.level.org.springframework.security=${SPRING_SECURITY_LOG_LEVEL:TRACE}
  - logging.level.org.springframework.security.web.FilterChainProxy=DEBUG

```
********************************************************************
**********        Security debugging is enabled.       *************
**********    This may include sensitive information.  *************
**********      Do not use in a production system!     *************
********************************************************************

Security filter chain: [
  DisableEncodeUrlFilter
  ForceEagerSessionCreationFilter
  ForceEagerSessionCreationFilter
  ChannelProcessingFilter
  WebAsyncManagerIntegrationFilter
  SecurityContextPersistenceFilter
  HeaderWriterFilter
  CorsFilter
  CsrfFilter
  LogoutFilter
  UsernamePasswordAuthenticationFilter
  DefaultLoginPageGeneratingFilter
  DefaultLogoutPageGeneratingFilter
  RequestValidationBeforeFilter
  AuthoritiesLoggingAtFilter
  BasicAuthenticationFilter
  CsrfCookieFilter
  AuthoritiesLoggingAfterFilter
  RequestCacheAwareFilter
  SecurityContextHolderAwareRequestFilter
  AnonymousAuthenticationFilter
  SessionManagementFilter
  ExceptionTranslationFilter
  AuthorizationFilter
]
```

# Opciones para implementar Filtros
1. Implementar la interface Filter
2. Extender de la class GenericFilterBean
3. Extender de la class OncePerRequestFilter (Garantiza que se ejecuta solo una vez por request)


# Implementando Filtro personalizado 1
- Requerimiento para Filtro.
  - Ej. No realizar authentication si el correo trae la palabra "test" y en su lugar lanzar un error
  - Se va configurar justo antes de BasicAuthenticationFilter
  - CorsFilter -> CsrfFilter -> RequestValidationFilter -> BasicAuthenticationFilter
  - Se usará la interface Filter que viene en Jakarta 
    ```
    public class RequestValidationBeforeFilter implements Filter {..}
    ```
  - Las credenciales están en una cabecera que vienen en : String header = req.getHeader(HttpHeaders.AUTHORIZATION);
    - Ej.  authorization: Basic aGFwcHlAZXhhbXBsZS5jb206RWF6eUJ5dGVzQDU0MzIx
  - Decodificar Token en Base64
  - Una vez decodificado viene en el formato : user:pwd
  - Verificar contenido y lanzar excepcion si fuera el caso
  - NO OLVIDAR LA LLAMADA AL SIGUIENTE FILTRO : chain.doFilter(request, response);
  - Agregar Filtro en las configuraciones de seguridad
```
    .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
```
- Desde postman probar /test con : happy@example.com         200 OK
- Desde postman probar /test con : happytest@example.com     400 Bad Request

# Implementando Filtro personalizado 2
- Requerimiento para Filtro.
  - Ej. Despues de la Authenticacion exitosa escribir en el logger los authorities del user
  - Se va configurar justo despues de BasicAuthenticationFilter
  - CorsFilter -> CsrfFilter -> BasicAuthenticationFilter -> AuthoritiesLoggingAfterFilter
  - Se usará la interface Filter que viene en Jakarta
  ```
  public class AuthoritiesLoggingAfterFilter implements Filter {...}
  ```
- Agregar Filtro en las configuraciones de seguridad
```
     .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
```
-  Salida en log
```
12:23:44.183 INFO  [http-nio-8080-exec-1] c.e.f.AuthoritiesLoggingAfterFilter - User happy@example.com is successfully authenticated and has the authorities [ROLE_ADMIN, ROLE_USER]
```

# Implementando Filtro personalizado 3
- Requerimiento para Filtro.
  - Ej. Mandar mensaje en el log de Authentication esta en proceso
  - Se usará la interface Filter que viene en Jakarta
  - NO SE RECOMIENDA ESTE ENFOQUE YA QUE NO SE ASEGURA EL ORDEN DE EJECUCION
```
public class AuthoritiesLoggingAtFilter implements Filter {...}
```
  - Agregar Filtro en las configuraciones de seguridad
```
     .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
```
  -  Salida en log
```
12:42:14.610 INFO  [http-nio-8080-exec-5] c.e.f.AuthoritiesLoggingAtFilter - Authentication Validation is in progress
```

 




