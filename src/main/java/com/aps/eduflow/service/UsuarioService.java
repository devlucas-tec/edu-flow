package com.aps.eduflow.service;

import com.aps.eduflow.domain.dto.CadastroUsuarioRequest;
import com.aps.eduflow.domain.dto.UsuarioResponseDTO;
import com.aps.eduflow.domain.dto.UsuarioUpdateRequestDTO;
import com.aps.eduflow.domain.entity.Usuario;
import com.aps.eduflow.domain.enums.UserRole;
import com.aps.eduflow.domain.exception.RegraNegocioException;
import com.aps.eduflow.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO cadastrar(CadastroUsuarioRequest dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail");
        }

        if (usuarioRepository.existsByMatricula(dto.getMatricula())) {
            throw new RegraNegocioException("Já existe um usuário com essa matrícula");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setMatricula(dto.getMatricula());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        // Auto-cadastro: role é sempre ALUNO
        usuario.setRole(UserRole.ALUNO);

        usuario = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(usuario);
    }

    public UsuarioResponseDTO cadastrarComRole(CadastroUsuarioRequest dto, UserRole roleAutorizado) {
        // ADMIN pode criar qualquer role; PROFESSOR pode criar MONITOR
        validarPermissaoCriacao(dto.getRole(), roleAutorizado);

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail");
        }

        if (usuarioRepository.existsByMatricula(dto.getMatricula())) {
            throw new RegraNegocioException("Já existe um usuário com essa matrícula");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setMatricula(dto.getMatricula());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setRole(dto.getRole());

        usuario = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(usuario);
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
        return new UsuarioResponseDTO(usuario);
    }

    public List<UsuarioResponseDTO> listarTodos(UserRole role) {
        List<Usuario> usuarios;
        if (role != null) {
            usuarios = usuarioRepository.findByRole(role);
        } else {
            usuarios = usuarioRepository.findAll();
        }
        return usuarios.stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateRequestDTO dto, UserRole roleAutorizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));

        // Verifica se o e-mail está sendo trocado e se já existe outro com esse e-mail
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail");
        }

        // Verifica se a matrícula está sendo trocada e se já existe outra com esse valor
        if (!usuario.getMatricula().equals(dto.getMatricula()) && usuarioRepository.existsByMatricula(dto.getMatricula())) {
            throw new RegraNegocioException("Já existe um usuário com essa matrícula");
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setMatricula(dto.getMatricula());

        usuario = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(usuario);
    }

    public UsuarioResponseDTO promoverRole(Long id, UserRole novaRole, UserRole roleAutorizado) {
        // ADMIN e PROFESSOR podem promover a MONITOR; só ADMIN pode criar PROFESSOR
        validarPermissaoCriacao(novaRole, roleAutorizado);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));

        usuario.setRole(novaRole);
        usuario = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(usuario);
    }

    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RegraNegocioException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private void validarPermissaoCriacao(UserRole roleDesejada, UserRole roleAutorizado) {
        if (roleAutorizado == UserRole.ADMIN) {
            return; // ADMIN pode qualquer coisa
        }

        if (roleAutorizado == UserRole.PROFESSOR && roleDesejada == UserRole.MONITOR) {
            return; // PROFESSOR pode promover a MONITOR
        }

        throw new RegraNegocioException("Você não tem permissão para atribuir o perfil " + roleDesejada.name());
    }
}
