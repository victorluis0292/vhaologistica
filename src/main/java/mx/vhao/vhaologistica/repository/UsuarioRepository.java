package mx.vhao.vhaologistica.repository;

import mx.vhao.vhaologistica.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ===============================
    // 🔐 LOGIN
    // ===============================
    Optional<Usuario> findByCorreo(String correo);

    // ===============================
    // ✔ Validar correo repetido
    // ===============================
    boolean existsByCorreo(String correo);

    // ===============================
    // 🔍 Buscador simple con parámetro (q)
    // ===============================
    @Query("""
           SELECT u FROM Usuario u
           WHERE (:q IS NULL OR 
                 LOWER(u.nombre)  LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(u.correo)  LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(u.rol)     LIKE LOWER(CONCAT('%', :q, '%')))
           """)
    List<Usuario> buscar(@Param("q") String q);

    // ===============================
    // 🔍 Buscador antiguo (multiparámetro + estado)
    // ===============================
    @Query("""
           SELECT u FROM Usuario u
           WHERE (:nombre IS NULL  OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
             AND (:correo IS NULL  OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%')))
             AND (:rol IS NULL     OR LOWER(u.rol) = LOWER(:rol))
             AND (:estado IS NULL  OR LOWER(u.estado) = LOWER(:estado))
           """)
    List<Usuario> buscarConEstado(
            @Param("nombre") String nombre,
            @Param("correo") String correo,
            @Param("rol") String rol,
            @Param("estado") String estado
    );

    // ===============================
    // 🔥 Buscador general (q + estadoFiltro)
    // ===============================
    @Query("""
           SELECT u FROM Usuario u
           WHERE (:estadoFiltro IS NULL OR LOWER(u.estado) = LOWER(:estadoFiltro))
             AND (:q IS NULL OR 
                 LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(u.correo) LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(u.rol)    LIKE LOWER(CONCAT('%', :q, '%')))
           """)
    List<Usuario> buscarGeneral(
            @Param("q") String q,
            @Param("estadoFiltro") String estadoFiltro
    );

    // ===============================
    // 👨‍🔧 LISTAR TÉCNICOS
    // ===============================
    @Query("""
           SELECT u FROM Usuario u
           WHERE UPPER(u.rol) = 'TECNICO'
           """)
    List<Usuario> findTecnicos();

    // ===============================
    // 🔍 Buscador predictivo de técnicos
    // ===============================
    @Query("""
           SELECT u FROM Usuario u
           WHERE UPPER(u.rol) = 'TECNICO'
             AND (
                 :q IS NULL OR 
                 LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(u.correo) LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(u.estado) LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(u.ciudad) LIKE LOWER(CONCAT('%', :q, '%'))
             )
           """)
    List<Usuario> buscarTecnicos(@Param("q") String q);

    // ===============================
    // 👨‍💼 COORDINADORES
    // ===============================
    @Query("""
           SELECT u FROM Usuario u
           WHERE UPPER(u.rol) = 'COORDINADOR'
           """)
    List<Usuario> listarCoordinadores();
}
