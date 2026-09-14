package com.senai.cadastro.application.service;

import com.senai.cadastro.application.dto.PerfilUpdateDTO;
import com.senai.cadastro.application.dto.UsuarioRequestDTO;
import com.senai.cadastro.application.dto.UsuarioResponseDTO;
import com.senai.cadastro.application.exception.UsuarioDuplicadoException;
import com.senai.cadastro.application.exception.UsuarioNaoEncontradoException;
import com.senai.cadastro.domain.entity.Usuario;
import com.senai.cadastro.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(UUID id) {
        return UsuarioResponseDTO.fromEntity(buscarEntidadePorId(id));
    }

    @Transactional
    public UsuarioResponseDTO save(UsuarioRequestDTO usuarioRequestDTO) {

        String email = normalizarEmail( usuarioRequestDTO.email());
        String cpf = normalizarCpf(usuarioRequestDTO.cpf());
        validarDuplicidadeNovoUsuario(email, cpf);

        Usuario usuario = usuarioRequestDTO.toEntity();

        usuario.setEmail(email);
        usuario.setCpf(cpf);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.senha()));

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO update(UUID id, UsuarioRequestDTO usuarioRequestDTO) {

        Usuario usuarioExistente = buscarEntidadePorId(id);
        String email = normalizarEmail(usuarioRequestDTO.email());
        String cpf = normalizarCpf(usuarioRequestDTO.cpf());
        validarDuplicidadeAtualizacao(id,email,cpf);

        usuarioExistente.setNome(usuarioRequestDTO.nome());
        usuarioExistente.setCpf(cpf);
        usuarioExistente.setEmail(email);
        usuarioExistente.setSenha(passwordEncoder.encode(usuarioRequestDTO.senha()));

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    public UsuarioResponseDTO updatePerfil(UUID id,PerfilUpdateDTO perfilUpdateDTO) {

        Usuario usuario = buscarEntidadePorId(id);
        usuario.setPerfil(perfilUpdateDTO.perfil());

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void delete(UUID id) {
        Usuario usuario = buscarEntidadePorId(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarEntidadePorId(UUID id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);
    }

    private void validarDuplicidadeNovoUsuario(String email, String cpf) {
        if (usuarioRepository.existsByEmailIgnoreCase(email))
            throw new UsuarioDuplicadoException("e-mail");

        if (usuarioRepository.existsByCpf(cpf))
            throw new UsuarioDuplicadoException("CPF");

    }
    private void validarDuplicidadeAtualizacao(UUID id,String email,String cpf) {
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email,id))
            throw new UsuarioDuplicadoException("e-mail");

        if (usuarioRepository.existsByCpfAndIdNot(cpf,id))
            throw new UsuarioDuplicadoException("CPF");
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarCpf(String cpf) {
        return cpf.replaceAll("\\D","");
    }
}