package com.tpintegrador.tecnicas_avanzadas_MP.security;

import com.tpintegrador.tecnicas_avanzadas_MP.exception.NoAutenticadoException;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .map(UsuarioPrincipal::new)
                .orElseThrow(() -> new NoAutenticadoException("Usuario no encontrado"));
    }
}


