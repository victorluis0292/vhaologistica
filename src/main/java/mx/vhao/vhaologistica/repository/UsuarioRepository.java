package mx.vhao.vhaologistica.repository;

import mx.vhao.vhaologistica.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Para login
    Optional<Usuario> findByCorreo(String correo);

    // Validar correo repetido
    boolean existsByCorreo(String correo);

    // Búsqueda normal (nombre, correo, rol)
    @Query("SELECT u FROM Usuario u " +
           "WHERE (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:correo IS NULL OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%'))) " +
           "AND (:rol IS NULL OR LOWER(u.rol) = LOWER(:rol))")
    List<Usuario> buscar(
            @Param("nombre") String nombre,
            @Param("correo") String correo,
            @Param("rol") String rol
    );

    // Búsqueda con filtrado por estado
    @Query("SELECT u FROM Usuario u " +
           "WHERE (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:correo IS NULL OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%'))) " +
           "AND (:rol IS NULL OR LOWER(u.rol) = LOWER(:rol)) " +
           "AND (:estado IS NULL OR LOWER(u.estado) = LOWER(:estado))")
    List<Usuario> buscarConEstado(
            @Param("nombre") String nombre,
            @Param("correo") String correo,
            @Param("rol") String rol,
            @Param("estado") String estado
    );
}
