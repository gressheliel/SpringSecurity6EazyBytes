# AuthenticationProvider Métodos
- DaoAuthenticationProvider se utiliza cuando no se provee una implementación
- public Authentication authenticate(final Authentication authentication) throws AuthenticationException {...}
- public boolean supports(Class<?> authentication) {...} 
    - // Tipo de authenticacion soportada
    - AbstractAuthenticationToken, JaasAuthenticationToken, OidcAuthenticationToken, RememberMeAuthenticationToken

# Implementación
- public class EazyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {...}
- user:pwd   (john@example.com, EazyBytes@12345)

# Definir variables de entorno desde Intellij
- Environment variables : SPRING_PROFILES_ACTIVE=default

# Uso de profiles
- Personalizar la implementación Ej.
  - Cuando se trabaja desde DEV, no se valida la contraseña
  - Desde PROD, se valida que el password sea correcto

# Profile PROD
- Crear application_prod.properties
  - spring.config.activate.on-profile= prod
  - Para PROD se modifica el nivel del log y del show_sql
- Implementaciones : 
  - @Profile("prod") EazyBankProdUsernamePwdAuthenticationProvider
  - @Profile("prod") ProjectSecurityProdConfig


# Profile DEFAULT
- En application.properties
  - spring.config.import = application_prod.properties,application_qa.properties
  - spring.profiles.active = default

