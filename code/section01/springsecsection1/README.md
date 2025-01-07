# 01 Project Base
- Uso de variables de entorno
  - Permite definir la variable en el entorno que será usada la App 
  - y sobreescribe el valor que se define en el *.properties. 
  - Si no se define la variable de entorno usa el valor que se encuentra en el *.properties.
- Buscar class Navigate -> Class...
- SecurityProperties, se define el user y pwd de la seguridad básica.

# 02 AI Coding Companion
- Codeium: Free AI-powered code acceleration

# 03 Flujo Usuario intenta acceder a recurso protegido
- AuthorizationFilter identifica si esta accediendo a una ruta protegida lanza : throw new AccessDeniedException("Access Denied")
- DefaultLoginPageGeneratingFilter  muestra la pagina de login : generateLoginPageHtml()

# 04 Flujo Usuario ingresa credenciales
- AbstractAuthenticationProcessingFilter -> UsernamePasswordAuthenticationFilter
- ProviderManager -> DaoAuthenticationProvider -> InMemoryUserDetailsManager

# 05 Flujo default
- User -> Filters ->AuthenticationManager -> AuthenticationProviders -> UserDetailsManager/Service -> PasswordEncoder

# 06 Llamadas sucesivas
- JSESSIONID Se guarda en el browser y se verifica con el SecurityContext, si se altera la cookie, 
  pedirá volver a loguearse.