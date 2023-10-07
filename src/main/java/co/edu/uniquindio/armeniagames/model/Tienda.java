package co.edu.uniquindio.armeniagames.model;

import co.edu.uniquindio.armeniagames.enumm.TipoUsuario;
import co.edu.uniquindio.armeniagames.exception.*;
import co.edu.uniquindio.armeniagames.interfacce.TiendaService;
import co.edu.uniquindio.armeniagames.persistence.Persistencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tienda{

    public Persistencia persistencia = new Persistencia();
    private final ArrayList<Videojuego> listaVideojuegos = new ArrayList<>();
    private final ArrayList<Jugador> listaJugadores = new ArrayList<>();
    private final ArrayList<Administrador> listaAdministradores = new ArrayList<>();
    private final ArrayList<Compra> listaCompras = new ArrayList<>();

    public Tienda() {
    }

    public boolean validarUsuario(Usuario usu) throws IOException {

        boolean esCorrecto = false;
        ArrayList<Administrador> administrador = persistencia.cargarAdministrador();
        ArrayList<Jugador> jugador = persistencia.cargarJugador();

        if (usu.getTipoUsuario().equals(TipoUsuario.Jugador)) {
            for (Jugador jug : jugador) {
                if (jug.getCorreo().equals(usu.getCorreo()) && jug.getClave().equals(usu.getClave())
                        && jug.getTipoUsuario().equals(usu.getTipoUsuario())) {
                    esCorrecto = true;
                    break;
                }
            }
        }

        if (usu.getTipoUsuario().equals(TipoUsuario.Administrador)) {
            for (Administrador admin : administrador) {
                if (admin.getCorreo().equals(usu.getCorreo()) && admin.getClave().equals(usu.getClave())
                        && admin.getTipoUsuario().equals(usu.getTipoUsuario())) {
                    esCorrecto = true;
                    break;
                }
            }
        }
        return esCorrecto;
    }

    public Usuario login(Usuario usuario)
            throws JugadorNoExisteException, AdministradorNoExisteException {

        Usuario usu = null;

        if (usuario.getTipoUsuario().equals(TipoUsuario.Administrador)) {

            for (int i = 0; i < getListaAdministradores().size();) {

                if (getListaAdministradores().get(i).getCorreo().equals(usuario.getCorreo())
                        && getListaAdministradores().get(i).getClave().equals(usuario.getClave())
                        && getListaAdministradores().get(i).getTipoUsuario().equals(usuario.getTipoUsuario())) {

                    usu = getListaAdministradores().get(i);
                    break;
                } else {
                    i++;
                }
            }
        }

        if (usuario.getTipoUsuario().equals(TipoUsuario.Jugador)) {

            for (int i = 0; i < getListaJugadores().size();) {

                if (getListaJugadores().get(i).getCorreo().equals(usuario.getCorreo())
                        && getListaJugadores().get(i).getClave().equals(usuario.getClave())
                        && getListaJugadores().get(i).getTipoUsuario().equals(usuario.getTipoUsuario())) {

                    usu = getListaJugadores().get(i);
                    break;
                } else {
                    i++;
                }
            }
        }
        return usu;
    }

    public boolean iniciarSesion(Usuario usuario)
            throws IOException, UsuarioNoExisteException {

        if (validarUsuario(usuario)) {
            return true;
        } else {
            throw new UsuarioNoExisteException();
        }
    }

    public Administrador guardarAdministrador(Administrador ad) throws JugadorExisteException, ContraseniasNoCoincidenException, ClaveNoSeguraException {

        Administrador administrador;
        boolean existeAdministrador = verificarAdministradorExiste(ad.getDocumento(), ad.getTipoUsuario());

        if (existeAdministrador) {
            throw new JugadorExisteException();
        } else if (claveIncorrecta(ad.getClave(), ad.getConfirmacionClave())) {
            throw new ContraseniasNoCoincidenException();
        } else if (!validarClave(ad.getClave())) {
            throw new ClaveNoSeguraException();
        }else{

            administrador = new Administrador();

            administrador.setDocumento(ad.getDocumento());
            administrador.setNombrePersona(ad.getNombrePersona());
            administrador.setApellido(ad.getApellido());
            administrador.setFechaNacimiento(ad.getFechaNacimiento());
            administrador.setTipoEstadoCivil(ad.getTipoEstadoCivil());
            administrador.setTipoGenero(ad.getTipoGenero());
            administrador.setCorreo(ad.getCorreo());
            administrador.setClave(ad.getClave());
            administrador.setConfirmacionClave(ad.getConfirmacionClave());
            administrador.setTipoUsuario(ad.getTipoUsuario());
            administrador.setTelefono(ad.getTelefono());
            administrador.setCarnet(ad.getCarnet());

            administrador.setImagen(ad.getImagen());

            getListaAdministradores().add(administrador);
        }
        return administrador;
    }

    public boolean eliminarVideojuego(String codigo) throws VideojuegoNoExisteException {

        Videojuego videojuego;

        videojuego = obtenerVideojuego(codigo);

        if (videojuego != null) {
            getListaVideojuegos().remove(videojuego);
        } else {
            throw new VideojuegoNoExisteException();
        }
        return true;
    }

    public boolean verificarAdministradorExiste(String documento, TipoUsuario tipoUsuario) {

        Administrador administrador;
        boolean existeAdministrador = false;

        if (tipoUsuario == TipoUsuario.Administrador) {
            administrador = obtenerAdministrador(documento);
            existeAdministrador = administrador != null;
        }
        return existeAdministrador;
    }

    public Jugador obtenerJugador(String documento) {

        Jugador jug = null;

        for (Jugador jugador : listaJugadores) {
            if (jugador.getDocumento().equals(documento)) {
                jug = jugador;
                break;
            }
        }
        return jug;
    }

    public Jugador obtenerJugador2(String correo) {

        Jugador jug = null;

        for (Jugador jugador : listaJugadores) {
            if (jugador.getCorreo().equals(correo)) {
                jug = jugador;
                break;
            }
        }
        return jug;
    }

    public Jugador guardarJugador(Jugador jug) throws JugadorExisteException, ContraseniasNoCoincidenException, ClaveNoSeguraException {

        Jugador jugador;
        boolean existeJugador = verificarJugadorExiste(jug.getDocumento(), jug.getTipoUsuario());

        if (existeJugador) {
            throw new JugadorExisteException();
        } else if (claveIncorrecta(jug.getClave(), jug.getConfirmacionClave())) {
            throw new ContraseniasNoCoincidenException();
        } else if (!validarClave(jug.getClave())) {
            throw new ClaveNoSeguraException();
        }else{

            jugador = new Jugador();

            jugador.setDocumento(jug.getDocumento());
            jugador.setNombrePersona(jug.getNombrePersona());
            jugador.setApellido(jug.getApellido());
            jugador.setFechaNacimiento(jug.getFechaNacimiento());
            jugador.setTipoEstadoCivil(jug.getTipoEstadoCivil());
            jugador.setTipoGenero(jug.getTipoGenero());
            jugador.setCorreo(jug.getCorreo());
            jugador.setClave(jug.getClave());
            jugador.setConfirmacionClave(jug.getConfirmacionClave());
            jugador.setTipoUsuario(jug.getTipoUsuario());
            jugador.setTelefono(jug.getTelefono());

            jugador.setTipoBanco(jug.getTipoBanco());
            jugador.setTipoCuenta(jug.getTipoCuenta());
            jugador.setNumeroCuenta(jug.getNumeroCuenta());
            jugador.setFechaCaducidad(jug.getFechaCaducidad());
            jugador.setTitular(jug.getTitular());

            jugador.setTipoResidencia(jug.getTipoResidencia());
            jugador.setCodigoPostal(jug.getCodigoPostal());
            jugador.setDireccion(jug.getDireccion());
            jugador.setBarrio(jug.getBarrio());
            jugador.setTipoDepartamento(jug.getTipoDepartamento());
            jugador.setMunicipio(jug.getMunicipio());
            jugador.setVideojuegosComprados(jug.getVideojuegosComprados());

            jugador.setImagen(jug.getImagen());
            jugador.setTipoRestriccion(jug.getTipoRestriccion());
            jugador.setIntentos(jug.getIntentos());

            getListaJugadores().add(jugador);
        }
        return jugador;
    }

    public Compra guardarCompra(Compra compra) {

        Compra comp = new Compra();

        comp.setDocumentoJugador(compra.getDocumentoJugador());
        comp.setFactura(compra.getFactura());
        comp.setJugador(compra.getJugador());
        comp.setApellido(compra.getApellido());
        comp.setCodigo(compra.getCodigo());
        comp.setTotal(compra.getTotal());
        comp.setNombreVideojuego(compra.getNombreVideojuego());
        comp.setTipoFormatoVideojuego(compra.getTipoFormatoVideojuego());
        comp.setTipoGeneroVideojuego(compra.getTipoGeneroVideojuego());
        comp.setFechaCompraInicial(compra.getFechaCompraInicial());
        comp.setFechaCompraFinal(compra.getFechaCompraFinal().plusMonths(3));

        getListaCompras().add(comp);

        return comp;
    }

    public boolean verificarJugadorExiste(String documento, TipoUsuario tipoUsuario) {

        Jugador jugador;
        boolean existeJugador = false;

        if (tipoUsuario == TipoUsuario.Jugador) {
            jugador = obtenerJugador(documento);
            existeJugador = jugador != null;
        }
        return existeJugador;
    }

    public Administrador obtenerAdministrador(String documento) {

        Administrador admin = null;

        for (Administrador administrador : listaAdministradores) {
            if (administrador.getDocumento().equals(documento)) {
                admin = administrador;
                break;
            }
        }
        return admin;
    }

    public Administrador obtenerAdministrador2(String correo) {

        Administrador admin = null;

        for (Administrador administrador : listaAdministradores) {
            if (administrador.getCorreo().equals(correo)) {
                admin = administrador;
                break;
            }
        }
        return admin;
    }

    public boolean claveIncorrecta(String clave, String confirmacion) {
        return !clave.equals(confirmacion);
    }

    public boolean validarClave(String clave) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(clave);
        return matcher.matches();
    }

    public Videojuego obtenerVideojuego(String codigo) {

        Videojuego vid = null;

        for (Videojuego videojuego : listaVideojuegos) {
            if (videojuego.getNombreVideojuego().equals(codigo)) {
                vid = videojuego;
                break;
            }
        }
        return vid;
    }

    public Compra obtenerCompra(int factura) {

        Compra comp = null;

        for (Compra compra : listaCompras) {
            if (compra.getFactura() == factura) {
                comp = compra;
                break;
            }
        }
        return comp;
    }

    public boolean verificarVideojuegoExiste(String codigo) {

        Videojuego videojuego;
        boolean existeVideojuego;

        videojuego = obtenerVideojuego(codigo);
        existeVideojuego = videojuego != null;

        return existeVideojuego;
    }

    public Videojuego mostrarDatosVideojuego(String codigo) throws VideojuegoNoExisteException {

        Videojuego videojuego;
        videojuego = obtenerVideojuego(codigo);

        if (videojuego != null) {

            for (int i = 0; i < getListaVideojuegos().size(); i++) {
                if (getListaVideojuegos().get(i).getNombreVideojuego().equals(codigo)) {

                    videojuego.setCodigo(videojuego.getCodigo());
                    videojuego.setNombreVideojuego(videojuego.getNombreVideojuego());
                    videojuego.setPrecio(videojuego.getPrecio());
                    videojuego.setTipoFormatoVideojuego(videojuego.getTipoFormatoVideojuego());
                    videojuego.setTipoGeneroVideojuego(videojuego.getTipoGeneroVideojuego());
                    videojuego.setAnioLanzamiento(videojuego.getAnioLanzamiento());

                    getListaVideojuegos().set(i, videojuego);
                }
            }
        } else {
            throw new VideojuegoNoExisteException();
        }
        return videojuego;
    }

    public Videojuego guardarVideojuego(Videojuego videojuego) throws VideojuegoExisteException, IOException {

        Videojuego vid;

        boolean existeVideojuego = verificarVideojuegoExiste(videojuego.getCodigo());

        if (existeVideojuego) {
            throw new VideojuegoExisteException();
        } else {
            vid = new Videojuego();

            vid.setCodigo(videojuego.getCodigo());
            vid.setNombreVideojuego(videojuego.getNombreVideojuego());
            vid.setPrecio(videojuego.getPrecio());
            vid.setTipoGeneroVideojuego(videojuego.getTipoGeneroVideojuego());
            vid.setTipoFormatoVideojuego(videojuego.getTipoFormatoVideojuego());
            vid.setAnioLanzamiento(videojuego.getAnioLanzamiento());
            vid.setClasificacion(videojuego.getClasificacion());
            vid.setUnidades(videojuego.getUnidades());
            vid.setImagenVideojuego(videojuego.getImagenVideojuego());

            getListaVideojuegos().add(vid);
        }
        return vid;
    }

    public boolean devolverVideojuego(int factura) throws CompraNoExisteException{

        boolean bandera = false;

        Compra com = obtenerCompra(factura);

        if(com == null){
            bandera = false;
            throw new CompraNoExisteException();
        }else{
            Videojuego vd = obtenerVideojuego(com.getCodigo());
            getListaCompras().remove(com);
            incrementarInventario(com.getCodigo(), vd.getUnidades());
            bandera = true;
        }
        return bandera;
    }

    public boolean actualizarVideojuego(Videojuego videojuego)
            throws VideojuegoNoExisteException {

        Videojuego vid;
        boolean videojuegoExiste = false;

        vid = obtenerVideojuego(videojuego.getCodigo());

        if (vid != null) {

            for (int i = 0; i < getListaVideojuegos().size(); i++) {
                if (getListaVideojuegos().get(i).getCodigo().equals(videojuego.getCodigo())) {

                    vid.setNombreVideojuego(videojuego.getNombreVideojuego());
                    vid.setPrecio(videojuego.getPrecio());
                    vid.setTipoFormatoVideojuego(videojuego.getTipoFormatoVideojuego());
                    vid.setTipoGeneroVideojuego(videojuego.getTipoGeneroVideojuego());
                    vid.setAnioLanzamiento(videojuego.getAnioLanzamiento());
                    vid.setClasificacion(videojuego.getClasificacion());
                    vid.setUnidades(videojuego.getUnidades());
                    vid.setAnioLanzamiento(videojuego.getAnioLanzamiento());

                    getListaVideojuegos().set(i, vid);
                    videojuegoExiste = true;
                }
            }
        } else {
            throw new VideojuegoNoExisteException();
        }
        return videojuegoExiste;
    }

    public void actualizarAdministrador(Administrador administrador) throws ClaveNoSeguraException{

        Administrador admin;

        admin = obtenerAdministrador(administrador.getDocumento());

        if (!validarClave(administrador.getClave())) {
            throw new ClaveNoSeguraException();
        }

        else if (admin != null) {

            for (int i = 0; i < getListaAdministradores().size(); i++) {
                if (getListaAdministradores().get(i).getDocumento().equals(administrador.getDocumento())) {

                    admin.setNombrePersona(administrador.getNombrePersona());
                    admin.setApellido(administrador.getApellido());
                    admin.setTelefono(administrador.getTelefono());
                    admin.setCorreo(administrador.getCorreo());
                    admin.setClave(administrador.getClave());
                    admin.setClave(administrador.getConfirmacionClave());
                    admin.setTipoEstadoCivil(administrador.getTipoEstadoCivil());
                    admin.setImagen(administrador.getImagen());

                    getListaAdministradores().set(i, admin);
                }
            }
        }
    }

    public boolean eliminarAdministrador(String documento) throws AdministradorNoExisteException {

        Administrador administrador;

        administrador = obtenerAdministrador(documento);

        if (administrador != null) {
            getListaAdministradores().remove(administrador);
        } else {
            throw new AdministradorNoExisteException();
        }
        return true;
    }

    public void actualizarJugador(Jugador jugador) throws ClaveNoSeguraException{

        Jugador jug;

        jug = obtenerJugador(jugador.getDocumento());

        if (!validarClave(jugador.getClave())) {
            throw new ClaveNoSeguraException();
        }

        else if (jug != null) {

            for (int i = 0; i < getListaJugadores().size(); i++) {
                if (getListaJugadores().get(i).getDocumento().equals(jugador.getDocumento())) {

                    jug.setNombrePersona(jugador.getNombrePersona());
                    jug.setApellido(jugador.getApellido());
                    jug.setTelefono(jugador.getTelefono());
                    jug.setCorreo(jugador.getCorreo());
                    jug.setClave(jugador.getClave());
                    jug.setConfirmacionClave(jugador.getConfirmacionClave());
                    jug.setTipoEstadoCivil(jugador.getTipoEstadoCivil());
                    jug.setImagen(jugador.getImagen());

                    getListaJugadores().set(i, jug);
                }
            }
        }
    }

    public boolean eliminarJugador(String documento) throws JugadorNoExisteException {

        Jugador jugador;

        jugador = obtenerJugador(documento);

        if (jugador != null) {
            getListaJugadores().remove(jugador);
        } else {
            throw new JugadorNoExisteException();
        }
        return true;
    }

    public boolean cambiarClaveJugador(String correo, String clave, String confirmacion)
            throws JugadorNoExisteException, ContraseniasNoCoincidenException, ClaveNoSeguraException {

        boolean claveCambiadaConExito = false;

        Jugador jugador;

        jugador = obtenerJugador2(correo);

        if (claveIncorrecta(clave, confirmacion)) {

            throw new ContraseniasNoCoincidenException();

        } else if (!validarClave(clave)) {
            throw new ClaveNoSeguraException();
        } else if (jugador != null) {

            for (int i = 0; i < getListaJugadores().size(); i++) {
                if (getListaJugadores().get(i).getCorreo().equals(correo)) {

                    jugador.setClave(clave);
                    jugador.setConfirmacionClave(confirmacion);

                    getListaJugadores().set(i, jugador);

                    claveCambiadaConExito = true;
                }
            }
        } else {
            throw new JugadorNoExisteException();
        }
        return claveCambiadaConExito;
    }

    public boolean cambiarClaveAdministrador(String correo, String clave, String confirmacion)
            throws AdministradorNoExisteException, ContraseniasNoCoincidenException, ClaveNoSeguraException {

        boolean claveCambiadaConExito = false;

        Administrador administrador;

        administrador = obtenerAdministrador2(correo);

        if (claveIncorrecta(clave, confirmacion)) {

            throw new ContraseniasNoCoincidenException();

        } else if (!validarClave(clave)) {
            throw new ClaveNoSeguraException();
        } else if (administrador != null) {

            for (int i = 0; i < getListaAdministradores().size(); i++) {
                if (getListaAdministradores().get(i).getCorreo().equals(correo)) {

                    administrador.setClave(clave);
                    administrador.setConfirmacionClave(confirmacion);

                    getListaAdministradores().set(i, administrador);

                    claveCambiadaConExito = true;
                }
            }
        } else {
            throw new AdministradorNoExisteException();
        }
        return claveCambiadaConExito;
    }

    public Administrador mostrarDatosAdministrador(String documento) throws AdministradorNoExisteException {

        Administrador administrador;

        administrador = obtenerAdministrador(documento);

        if (administrador != null) {

            for (int i = 0; i < getListaAdministradores().size(); i++) {
                if (getListaAdministradores().get(i).getDocumento().equals(documento)) {

                    administrador.setNombrePersona(administrador.getNombrePersona());
                    administrador.setApellido(administrador.getApellido());
                    administrador.setTelefono(administrador.getTelefono());
                    administrador.setCorreo(administrador.getCorreo());
                    administrador.setImagen(administrador.getImagen());

                    getListaAdministradores().set(i, administrador);
                }
            }
        } else {
            throw new AdministradorNoExisteException();
        }
        return administrador;
    }

    public Jugador mostrarDatosJugador(String documento) throws JugadorNoExisteException {

        Jugador jugador;

        jugador = obtenerJugador(documento);

        if (jugador != null) {

            for (int i = 0; i < getListaJugadores().size(); i++) {
                if (getListaJugadores().get(i).getDocumento().equals(documento)) {

                    jugador.setNombrePersona(jugador.getNombrePersona());
                    jugador.setApellido(jugador.getApellido());
                    jugador.setTelefono(jugador.getTelefono());
                    jugador.setCorreo(jugador.getCorreo());
                    jugador.setImagen(jugador.getImagen());

                    getListaJugadores().set(i, jugador);
                }
            }
        } else {
            throw new JugadorNoExisteException();
        }
        return jugador;
    }

    public int generarNumAleatorio() {
        return (int) (Math.random() * 100 + 1);
    }

    public static int generarNumAleatorio2() {
        Random rand = new Random();
        return rand.nextInt(900000) + 100000;
    }

    public void disminuirInventario(String videojuego, int inventarioActual) {

        Videojuego vid;

        vid = obtenerVideojuego(videojuego);

        if (vid != null) {

            for (int i = 0; i < getListaVideojuegos().size(); i++) {
                if (getListaVideojuegos().get(i).getNombreVideojuego().equals(videojuego)) {

                    vid.setUnidades(inventarioActual - 1);

                    getListaVideojuegos().set(i, vid);
                }
            }
        }
    }

    public void incrementarInventario(String videojuego, int inventarioActual) {

        Videojuego vid;

        vid = obtenerVideojuego(videojuego);

        if (vid != null) {

            for (int i = 0; i < getListaVideojuegos().size(); i++) {
                if (getListaVideojuegos().get(i).getNombreVideojuego().equals(videojuego)) {

                    vid.setUnidades(inventarioActual + 1);

                    getListaVideojuegos().set(i, vid);
                }
            }
        }
    }

    public String chat(int opcion) throws InterruptedException {

        String respuesta;

        switch (opcion) {
            case 1
                ->
                 {
                    Thread.sleep(1500);
                    respuesta = "Armenia Games es una empresa dedicada al alquile de videojuegos en" +
                            "multiples formatos, contando con una gran variedad y gama junto con exclusivos" +
                            "que solo podrás encontrar aquí, ¡BIENVENIDO Y ANIMATE A COMPRAR!";
                }
            case 2
                ->
                 {
                    Thread.sleep(1500);
                    respuesta = "Nuestra dirección es Parque residencial San José torre 13 apartamento 402" +
                            " Armenia, Quindio";
                }
            case 3
                ->
                 {
                    Thread.sleep(1500);
                    respuesta = """
                            armeniagamess@gmail.com\s

                            armeniaagames@hotmail.com""";
                }
            case 4
                ->
                 {
                    Thread.sleep(1500);
                    respuesta = "3157566407";
                }
            case 5
                ->
                 {
                    Thread.sleep(1500);
                    respuesta = """
                            Eduardo Cortes Pineda\s
                            Contacto: eduardcpineda@gmail.com
                            eduardcpineda@hotmail.com""";
                }

            default
                ->
                 {
                    Thread.sleep(1500);
                    respuesta = "Respuesta incorrecta";
                }
        }
        return respuesta;
    }

    public void actualizarHistorial(String jugador, int comprados) {

        Jugador jug;

        jug = obtenerJugador(jugador);

        if (jug != null) {

            for (int i = 0; i < getListaJugadores().size(); i++) {
                if (getListaJugadores().get(i).getDocumento().equals(jugador)) {
                    jug.setVideojuegosComprados(comprados);
                    getListaJugadores().set(i, jug);
                }
            }
        }
    }

    public void actualizarHistorial2(String jugador, int comprados) {

        Jugador jug;

        jug = obtenerJugador(jugador);

        if (jug != null) {

            for (int i = 0; i < getListaJugadores().size(); i++) {
                if (getListaJugadores().get(i).getDocumento().equals(jugador)) {
                    jug.setVideojuegosComprados(comprados);
                    getListaJugadores().set(i, jug);
                }
            }
        }
    }

    public ArrayList<Videojuego> getListaVideojuegos() {
        return listaVideojuegos;
    }

    public ArrayList<Jugador> getListaJugadores() {
        return listaJugadores;
    }

    public ArrayList<Administrador> getListaAdministradores() {
        return listaAdministradores;
    }

    public ArrayList<Compra> getListaCompras() {
        return listaCompras;
    }

}