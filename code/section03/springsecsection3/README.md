# 01 Administración de usuarios InMemoryUserDetailsManager
- .password("{noop}12345")  Para definir que la contraseña no esta encriptada
- El passwordEncoder por default es : bcrypt en PasswordEncoderFactories
- Otras Opciones disponibles que implementan PasswordEncode
  - AbstractPasswordEncoder, Argon2PasswordEncoder, BCryptPasswordEncoder
  - DelegatingPasswordEncoder, LazyPasswordEncoder in AuthenticationConfiguration

- Se puede cifrar cadenas en : https://bcrypt-generator.com/

# 02 CompromisedPasswordChecker
- Disponible para la version spring-security 6.3
- Evita que se usen contraseñas simples
- Permite detectar passwords débiles ó comprometidas, no permite el acceso y obliga a cambiarlo
-  user : EazyBytes@12345   {noop}EazyBytes@12345
-  admin: EazyBytes@54321   {noop}$2a$12$88.f6upbBvy0okEa7OfHFuorV29qeK.sVbB9VQ6J6dWM1bW6Qef8m

# 03 UserDetailsService and UserDetailsManager
             UserDetailService <<interface>>              loadByUsername(String username)
                        |
             UserDetailsManager <<interface>>           createUser, updateUser, deleteUser, changePassword, userExists
              /           |           \
     InMemoryUser      JdbcUser      LdapUser            Sample Implemetation
    DetailsManager  DetailsManager  DetailsManager
_______________________________________________________
|              UserDetails                             |   Usuario Final
-------------------------------------------------------

# UserDetails and Authentication

User Credentials  ->  Filters   -> Authentication -> Authentication Manager -> Authentication Provider -> UserDetailService/Manager
                      Auth         Auth               Auth                     Auth                       Auth <--> UserDetails 

- La conversion desde un obj Authentication a UserDetails y viceversa ocurre en la capa UserDetailService/Manager
- Si todo ocurrió OK, se devuelve un Authentication con isAuthenticated() = true; 
- Authentication : Es el tipo de retorno donde se está tratando de determinar si la autenticacion fue
                   exitosa o no, AuthenticationManager AuthenticationProvider.
- UserDetails : Es el tipo de retorno donde se recupera información del usuario de los sistemas de almacenamiento
                UserDetailService UserDetailsManager.

