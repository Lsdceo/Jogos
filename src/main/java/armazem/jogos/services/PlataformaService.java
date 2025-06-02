package armazem.jogos.services;

import armazem.jogos.entities.Plataforma;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.PlataformaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class PlataformaService {
    @Autowired
     PlataformaRepository plataformaRepository;

    public List<Plataforma> listarTodas() {
        return plataformaRepository.findAll();
    }

    public Plataforma buscarPorId(Long id) {
        return plataformaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plataforma não encontrada com id: " + id));
    }

    @Transactional
    public Plataforma salvar(Plataforma plataforma) {
        plataformaRepository.findByNome(plataforma.getNome()).ifPresent(p -> {
            if (!p.getId().equals(plataforma.getId())) { // Permite atualizar o mesmo objeto
                throw new IllegalArgumentException("Plataforma com nome '" + plataforma.getNome() + "' já existe.");
            }
        });
        return plataformaRepository.save(plataforma);
    }

    @Transactional
    public Plataforma atualizar(Long id, Plataforma plataformaDetalhes){
        Plataforma plataforma = buscarPorId(id);
        plataforma.setNome(plataformaDetalhes.getNome());
        return salvar(plataforma);
    }


    @Transactional
    public void deletar(Long id) {
        if (!plataformaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plataforma não encontrada para deleção com id: " + id);
        }
        // Adicionar lógica para verificar se a plataforma está em uso (em ItemEstoque, Movimentacao) antes de deletar
        // if (itemEstoqueRepository.findByPlataforma(buscarPorId(id)).size() > 0) {
        // throw new DataIntegrityViolationException("Plataforma não pode ser deletada pois está em uso.");
        // }
        plataformaRepository.deleteById(id);
    }
}
