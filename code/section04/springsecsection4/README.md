# 01 Estructura de JdbcUserDetailsManager
- JdbcUserDetailsManager extends JdbcDaoImpl 
- Desde JdbcDaoImpl se tiene :  
  - public static final String DEFAULT_USER_SCHEMA_DDL_LOCATION = "org/springframework/security/core/userdetails/jdbc/users.ddl";
- Abriendo la dependencia :
  - C:\Users\Asus\.m2\repository\org\springframework\security\spring-security-core\6.4.2
- Se tiene :
```
create table users(username varchar_ignorecase(50) not null primary key,password varchar_ignorecase(500) not null,enabled boolean not null);
create table authorities (username varchar_ignorecase(50) not null,authority varchar_ignorecase(50) not null,constraint fk_authorities_users foreign key(username) references users(username));
create unique index ix_auth_username on authorities (username,authority);
```
- Para MySql , sustituir varchar_ignorecase(50) por varchar(50), Ejecutar scripts.sql

# 02 Agregar dependencias
- spring-boot-starter-data-jpa, spring-boot-starter-jdbc, mysql-connector-j, lombok

# 03 Configuración UserDetailsService y JdbcUserDetailsManager tablas propuestas por Spring documentacion
- JdbcUserDetailsManager Obliga a utilizar un esquema predefinido
- Con las tablas user & authorities y el @Bean
```
@Bean
public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
```
- user:EazyBytes@12345, admin:EazyBytes@54321

# 04 Configuración UserDetailsService y Class implements UserDetailsService tabla personalizada (Customer)
- Creación de table customer:
  - happy@example.com:EazyBytes@12345, admin@example.com:EazyBytes@54321
- Creación de classes :
  - Customer Entity 
  - CustomerRepository
  - EazyBankUserDetailsService implements UserDetailsService
- Comentar el bean : @Bean public UserDetailsService de ProjectSecurityConfig
- Agregar Apis para registrar un User : UserController



