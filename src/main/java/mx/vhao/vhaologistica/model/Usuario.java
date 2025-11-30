package mx.vhao.vhaologistica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String nombre;

    @Column(unique = true)
    private String correo;

    private String pass;

    private String rol;

    // Getters y setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
