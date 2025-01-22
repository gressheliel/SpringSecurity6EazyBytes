# Authorization & Authentication
- Class e Interfaces importantes
  - GrantedAuthority -> SimpleGrantedAuthority -> UserDetails ->Authentication

# Cambiar schema DB
- Un usuario puede tener multiples roles
- Agregar authorities table 
- Agregar al customer_id=1 = (VIEWACCOUNT, VIEWCARDS, VIEWLOANS, VIEWBALANCE)
- Agregar Entity Authority y cambios a Customer para la relación
- EazyBankUserDetailsService, recupera Authorities de la BD y transforma en: SimpleGrantedAuthority
```
List<GrantedAuthority> authorities = customer.getAuthorities()
              .stream()
              .map(authority -> new SimpleGrantedAuthority(authority.getName()))
              .collect(Collectors.toList());
```
- Desde postman llamar a : /user
- ProjectSecurityConfig configurar Autorization, con los Authorities
```
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
                        .requestMatchers("/myBalance").hasAnyAuthority("VIEWBALANCE", "VIEWACCOUNT")
                        .requestMatchers("/myLoans").hasAuthority("VIEWLOANS")
                        .requestMatchers("/myCards").hasAuthority("VIEWCARDS")
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll());

```
- Desde postman llamar a /myAccount con (john@example.com, EazyBytes@12345)
  - Como john@example.com no tiene ningun Authority, manda un 403


# Role vs Authority
- Authority =>  Accion especifica              Ej. VIEWACCOUNTS, VIEWCARDS
- Role      =>  Conjunto o grupo de acciones   Ej. ROLE_ADMIN, ROLE_USER
- Authority =>  Restringe acceso de manera fina              
- Role      =>  Restringe acceso de manera detallada
- Authority =>  Se puede personalizar(Debe ser con un método estático) : 
```
@Bean static GrantedAuthorityDefaults grantedAuthorityDefaults(){
  return new GrantedAuthorityDefaults("MIPREFIX_");
}
```
- Role      =>  Tiene prefijo ROLE

# Configurando Roles
- NO debe iniciar con ROLE_ se agrega automaticamente
```
 INSERT INTO `authorities` (`customer_id`, `name`) VALUES (1, 'ROLE_USER');
 INSERT INTO `authorities` (`customer_id`, `name`) VALUES (1, 'ROLE_ADMIN');
```
```
      .authorizeHttpRequests((requests) -> requests
          .requestMatchers("/myAccount").hasRole("USER")
          .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
          .requestMatchers("/myLoans").hasRole("USER")
          .requestMatchers("/myCards").hasRole("USER")
          .requestMatchers("/user").authenticated()
          .requestMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll());
```
- Desde postman llamar a /myAccount, /myCards, /myLoans, /myBalance con (john@example.com, EazyBytes@12345)
  - Como john@example.com no tiene ningun ROLE, manda un 403

- Desde postman llamar a /myAccount, /myCards, /myLoans, /myBalance con (happy@example.com, EazyBytes@12345)
  - Como happy@example.com tiene ROLE_ADMIN y ROLE_USER puede acceder

# Listening Authorization Events
- En caso de @EventListener public void onSuccess(AuthorizationGrantedEvent event){...}
- Spring Security NO recomienda el evento de Authorización Concedida, podría ralentizar la app o ser muy verboso en los log
- Checar documentación : https://docs.spring.io/spring-security/reference/servlet/authorization/events.html
```
@Component
@Slf4j
public class AuthorizationEvents {

    @EventListener
    public void onFailure(AuthorizationDeniedEvent deniedEvent) {
        log.error("Authorization failed for the user : {} due to : {}", deniedEvent.getAuthentication().get().getName(),
                deniedEvent.getAuthorizationDecision().toString());
    }
}
```
