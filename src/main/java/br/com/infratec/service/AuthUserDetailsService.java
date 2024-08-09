package br.com.infratec.service;

import br.com.infratec.model.TbUsuario;
import br.com.infratec.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AuthUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<TbUsuario> tbUsuario = usuarioRepository.findByLogin(login);

        if (tbUsuario.isEmpty()) {
            throw new UsernameNotFoundException("username not found:" + login);
        }

        return User
                .withUsername(tbUsuario.get().getLogin())
                .password(tbUsuario.get().getSenha())
                //.authorities(AuthorityUtils.createAuthorityList(_user.getRoles().toArray(new String[0])))
                //.roles(tbUsuario.getRoles().toArray(new String[0]))
                //.accountLocked(!tbUsuario.isActive())
                //.accountExpired(!tbUsuario.isActive())
                //.disabled(!tbUsuario.isActive())
                //.credentialsExpired(!tbUsuario.isActive())
                .build();
    }
}
