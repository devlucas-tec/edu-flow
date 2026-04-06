package com.aps.eduflow.infra.security;

import com.aps.eduflow.domain.entity.Usuario;
import com.aps.eduflow.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .map(DetailsUsuario::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }

    private record DetailsUsuario(Usuario usuario) implements UserDetails {

        @Override
        public String getUsername() {
            return usuario.getEmail();
        }

        @Override
        public String getPassword() {
            return usuario.getSenha();
        }

        @Override
        public java.util.Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return java.util.List.of(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + usuario.getRole())
            );
        }
    }
}
