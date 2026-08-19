package mx.com.adrian.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mx.com.adrian.demo.entity.UsuarioEntity;
import mx.com.adrian.demo.repository.UsuarioJpaRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioJpaRepository usuarioRepository;
    public CustomUserDetailsService(UsuarioJpaRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        return usuario;
    }
}