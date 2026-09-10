package com.grupo37.oficinamecanica.seguranca.service;

import com.grupo37.oficinamecanica.seguranca.model.Perfil;
import com.grupo37.oficinamecanica.seguranca.model.Usuario;
import com.grupo37.oficinamecanica.seguranca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalheUsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private DetalheUsuarioService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new DetalheUsuarioService();
        var field = DetalheUsuarioService.class.getDeclaredField("usuarioRepository");
        field.setAccessible(true);
        field.set(service, usuarioRepository);
    }

    @Test
    void deveCarregarUsuarioQuandoLoginExiste() {
        Usuario usuario = new Usuario(null, "joao", "senha", Perfil.GERENTE);
        when(usuarioRepository.findByLogin("joao")).thenReturn(Optional.of(usuario));

        UserDetails resultado = service.loadUserByUsername("joao");

        assertThat(resultado.getUsername()).isEqualTo("joao");
        verify(usuarioRepository).findByLogin("joao");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("inexistente"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }
}

