# CORS CSRF
- Para esta parte se debe importar un nuevo schema de BD. 
  - Contiene tablas(customer, accounts, account_transactions)
  - users => (happy@example.com, EazyBytes@54321),(john@example.com, EazyBytes@12345)
  - NoticeController : cacheControl Guarda los datos en cache la primera vez,
    * Si el usuario hace llamadas sucesivas dentro de los siguientes 60 Seg.
    * No va a la BD se obtienen del cache
  - Customer : @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) private String pwd
    * Siempre se va enviar del front -> back.  NUNCA EN SENTIDO CONTRARIO. Estaríamos enviando datos sensibles.
    * Access setting that means that the property may only be written (set) as part of deserialization 
    * (using "setter" method, or assigning to Field, or passed as Creator argument) 
    * but will not be read (get) for serialization, that is, the value of the property is not included 
    * in serialization.

## Registrar nuevo customer
- Con ayuda de postman  /register

# CORS Access to fetch at 'XXXURL' from origin 'http://localhost:4200' has been blocked by CORS policy
- Cross Origin Resource Sharing
- Suceden de lado del browser
- Protocolo que activa scripts corriendo sobre el navegador del cliente
  para interactuar con recursos de diferente origen(URL, Protocolo, Puerto ) 

## Solutions CORS
- @CorsOrigin(origins="http://localhost:4200") Puede estar en los controller
```
http.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
```

# CSRF
- Cross Site Request Forgery
- "Peticiones Falsificadas"
- La política por default que proporciona Spring, es detener todos los métodos que guardan, borran o actualizan datos
- Comentando //http.csrf(csrfConfig->crsfConfig.disable()), Marca un 403 Forbidden

## SOLUTION CSRF
- Determinar si la peticion es generada correctamente desde la app del user, ya que la peticion falsificada 
  y la correcta contendrán el JSESSIONID, relacionada con el inicio de session.
- Agregar un CSRF TOKEN(valor aleatorio) desde la app del user para diferenciar ambas peticiones
- El backend va enviar 2 cookies JSESSIONID(Relacionada con el inicio de session) y CSRF(TOKEN)
- Para que el back reconozca como una operacion valida el CSRF debe estar en la cookie y en el request
  - El hacker puede enviar la cookie, pero no la del request
- Class importantes
  - CsrfToken, DefaultCsrfToken, CsrfTokenRepository, CookieCsrfTokenRepository, CsrfFilter

## IMPLEMENTATION CSRF BACKEND
```
public class CsrfCookieFilter extends OncePerRequestFilter {...}

// ProjectSecurityConfig
CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
  .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
  .cors(...)
  .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))                       
  .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)  // Para asegurar que se genere el CSRF despues del login se agrega BasicAuth
```
## TESTING WITH POSTMAN
- GET   /user ,   Al llamar el login se crearán las 2 cookies(JSESSIONID, XSRF-TOKEN)
- POST  /contact, Pedirá el token CSRF, de lo contrario  marcará error:
  -  Invalid CSRF Token 'XXXXXXXXXX' was found on the request parameter '_csrf' or header 'X-XSRF-TOKEN'.
  - Se debe agregar en los Headers el token agregado en el login, X-XSRF-TOKEN


## IMPLEMENTATION CSRF FRONTEND
- Se guarda como : XSRF-TOKEN  se lee como : X-XSRF-TOKEN desde la UI
- El Login recupera y guarda la cookie CSRF
```
validateUser(loginForm: NgForm) {
    this.loginService.validateLoginDetails(this.model).subscribe(
      responseData => {
        this.model = <any> responseData.body;
        this.model.authStatus = 'AUTH';
        window.sessionStorage.setItem("userdetails",JSON.stringify(this.model));
        let xsrf = getCookie("XSRF-TOKEN")!;
        window.sessionStorage.setItem("XSRF-TOKEN",xsrf);
        this.router.navigate(['dashboard']);
      });
  }
```
- En el interceptor, recupera la cookie y la envía como parte del HeaderRequest
```
    let xsrf = sessionStorage.getItem('XSRF-TOKEN');
    if(xsrf){
      httpHeaders = httpHeaders.append('X-XSRF-TOKEN', xsrf);
    }
```
- En los servicios asegurar que se envíe como parte del Request y de la Cookie
```
{ observe: 'response',withCredentials: true }
```

- 1. Entrar a : http://localhost:4200/home
- 2. Entrar a Contact Us(Sin información del TOKEN CSRF) , con :
     {
     "contactName": "Madan Reddy",
     "contactEmail": "tutor@eazybytes.com",
     "subject": "Need a new saving account",
     "message": "I want to open a new saving account in EazyBank"
     }
- 3. Marcará un error 403, desde la consola del browser
- 4. Acceder a Login  como : (happy@example.com, EazyBytes@54321)
- 5. Se muestra el dashboard con los datos del usuario logeado.
- 6. Se hace el logout.
  - Esta comentado : // window.sessionStorage.setItem("XSRF-TOKEN","");
  - No borra el CSRF_TOKEN Solo los datos del User
- 7. Entrar a Contact Us(Con info del TOKEN CSRF) , con :
     {
     "contactName": "Madan Reddy",
     "contactEmail": "tutor@eazybytes.com",
     "subject": "Need a new saving account",
     "message": "I want to open a new saving account in EazyBank"
     }
- 8. Permite el envío del mensaje 


# Ignorando CSRF para paginas publicas.
- /contac-us, /register se convierten en paginas públicas
```
.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers( "/contact","/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
```
- Desde postman se puede invocar /contact, sin el token

