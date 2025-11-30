package mx.vhao.vhaologistica.service;

import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    public String login(String correo, String pass) {

        Usuario user = repo.findByCorreo(correo).orElse(null);

        if (user == null) {
            return "USER_NOT_FOUND";
        }

        if (!user.getPass().equals(pass)) {
            return "INVALID_PASSWORD";
        }

        return "OK";
    }

    public Usuario getByCorreo(String correo) {
        return repo.findByCorreo(correo).orElse(null);
    }
}
