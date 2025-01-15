# HTTPS
- user,pwd => (john@example.com, EazyBytes@12345)
- Desde: springsecsection7,  SecurityConfig
  - PROD    ProjectSecurityProdConfig => .requiresChannel(rcc -> rcc.anyRequest().requiresSecure())   // Only HTTPS
  - DEFAULT ProjectSecurityConfig     => .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // Only HTTP

# Handling Exception & Implementation 
   FILTER                      EXCEPTION TROWN                                HANDLER
- ExceptionTraslationFilter -> AuthenticationException  HTTP.STATUS  401  -> AuthenticationEntryPoint
- ExceptionTraslationFilter -> AccessDeniedException    HTTP.STATUS  403  -> AccessDeniedHandler

- Desde: springsecsection7
- public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint 
    - Testing con bad user (johnXXX@example.com, EazyBytes@12345)
    - ProjectSecurityConfig : http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
    - Se puede declarar globalmente en  http.exceptionHandling(....)
- public class CustomAccessDeniedHandler implements AccessDeniedHandler   
    - Testing con bad url : /myAccount1
    - ProjectSecurityConfig :  http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
    - Para una App MVC :  http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()).accessDeniedPage("/denied"));

# Session TimeOut
- Desde: springsecsection7
- Cookie JSESSIONID es creada por Spring, tiene 30 min de vida
- Personalizada Ej.
  - Spring No permite configurar menos de 2 min
  - application.properties : server.servlet.session.timeout=${SESSION_TIMEOUT:10m} 
- Redirecciona a esta URL cuando la session es inválida
  - ProjectSecurityConfig  : http.sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession"));

# Concurrent Sessions
- ProjectSecurityConfig  :  http.sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true))
- Abrir una session con Postman y luego desde el browser, Al intentar consumir /myAccount envía el mensaje:
  - "Maximum sessions of 1 for this principal exceeded"

# Session Fixation  SessionHijacking
- Por defecto Spring Security sua changeSessionId
- Fijación de la session. Es un riesgo potencial cuando un atacante crea una session para acceder a un sitio y entonces
  persuadir a otro usuario para logearse con la misma session
  - Spring Security  se previene con :
    - changeSessionId. No crea una nueva session, solo cambia el ID(Solo disponible Servlet 3.1 JEE 7 y posteriores)
    - newSession. Crea una session limpia, sin copiar datos de la session existente.
    - migrateSession. Crea una nueva session, y copia todos los datos de la session existente.
  ```
  http
       .sessionManagment(session -> session.sessionFixation(sessionFix -> sessionFix.newSession()));
  ```
- Secuestro de la session. Sucede cuando un atacante roba el ID de session, el ID puede estar en la URL o en una cookie.
  Una vez robado, puede hacer cosas en nombre del usuario.
  - Spring Security se previene con el uso de HTTPS y restringiendo el tiempo de la session.

# Listening Authentication 
- Desde: springsecsection7 : AuthenticationEvents
- Postman /myAccount  => (john@example.com, EazyBytes@12345) AuthenticationEvents onSuccess
- Postman /myAccount  => (john@example.com1, EazyBytes@12345) AuthenticationEvents onFailure

# Application MVC, Options Form Login
- Desde: eazyschool-end
  - Usuarios : user:EazyBytes@12345, admin:EazyBytes@12345
  - WebConfig implements WebMvcConfigurer,  son accesos a a páginas estáticas no tienen controller
  - Básicamente realizan funciones equivalentes:
    - Solo Manejo de URLs
      - .defaultSuccessUrl("/dashboard").failureUrl("/login?error=true")  
    - Spring Security da prioridad a estos, Manejo programático, envío de correo, entrada a bitácora
      - .successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler))

## Spring Security Dialect
- Dependencias : thymeleaf-extras-springsecurity6, spring-boot-starter-thymeleaf
- Taglib Thymeleaf : 
```
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```
- Taglib ThymeLeaf and Security : 
```
<html lang="en" xmlns:th="http://www.thymeleaf.org"
                xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity6">
```
- Taglib JSP
```
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
...
<security:authorize access="isAuthenticated()">
  Autenticated as <security:authentication property="principal.username" />
</security:authorize>
```
- sec:authorize="isAnonymous()"  : Render el contenido si el usuario no esta logeado.
- sec:authorize="isAuthenticate()" : Render el contenido cuando la expresion es evaluada a true
- sec:authentication="hasRole('ROLE_USER')" : Imprime usuario logeado y sus roles

## SecurityContextHolder & SecurityContext
- SecurityContextHolder -> SecurityContext -> Authentication -> (Principal, Credentials, Authorities)
- Cargar los detalles del user logged, en cualquier capa, controller, service
```
@GetMapping(value="/username")
public String currentUserName1(){
  Authentication authentication = SecurityContextHoldel.getContext().getAuthentication();
  return authentication.getName();
}
```
- Cargar los detalles del user logged, en capa controller
```
- @GetMapping(value="/username")
public String currentUserName2(Authentication authentication){
  return authentication.getName();
}
```
