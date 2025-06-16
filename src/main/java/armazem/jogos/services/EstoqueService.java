package armazem.jogos.services;

import armazem.jogos.dtos.ItemEstoqueDTO; // Importe o DTO
import armazem.jogos.dtos.MovimentacaoRequestDTO;
import armazem.jogos.entities.*;
import armazem.jogos.exception.InsufficientStockException;
import armazem.jogos.exception.ResourceNotFoundException;
import armazem.jogos.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstoqueService {

    @Autowired ItemEstoqueRepository itemEstoqueRepository;
    @Autowired MovimentacaoRepository movimentacaoRepository;
    @Autowired JogoRepository jogoRepository;
    @Autowired PlataformaRepository plataformaRepository;
    @Autowired DepositoRepository depositoRepository;

    @Transactional
    public Movimentacao registrarMovimentacao(MovimentacaoRequestDTO request) {
        Jogo jogo = jogoRepository.findById(request.getJogoId())
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado: " + request.getJogoId()));
        Plataforma plataforma = plataformaRepository.findById(request.getPlataformaId())
                .orElseThrow(() -> new ResourceNotFoundException("Plataforma não encontrada: " + request.getPlataformaId()));

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setJogo(jogo);
        movimentacao.setPlataforma(plataforma);
        movimentacao.setQuantidade(request.getQuantidade());
        movimentacao.setTipo(request.getTipo());
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setPrecoUnitarioMomento(request.getPrecoUnitarioMomento() != null ? request.getPrecoUnitarioMomento() : jogo.getPrecoSugerido());
        movimentacao.setObservacao(request.getObservacao());

        switch (request.getTipo()) {
            case ENTRADA:
            case AJUSTE_POSITIVO:
                handleEntrada(request, jogo, plataforma, movimentacao);
                break;
            case SAIDA:
            case AJUSTE_NEGATIVO:
                handleSaida(request, jogo, plataforma, movimentacao);
                break;
            // REMOVIDO O CASE TRANSFERENCIA QUE CAUSAVA O ERRO
            case TRANSFERENCIA_SAIDA: // Mantenha estes casos se MovimentacaoRequestDTO pode vir com estes tipos
            case TRANSFERENCIA_ENTRADA: // Mantenha estes casos se MovimentacaoRequestDTO pode vir com estes tipos
                // Se o DTO de movimentação pode ter estes tipos, você precisa implementar a lógica aqui ou
                // garantir que apenas registrarTransferencia seja chamado com esses tipos.
                throw new UnsupportedOperationException("Movimentações de transferência devem ser registradas usando o método 'registrarTransferencia'.");
            default:
                throw new IllegalArgumentException("Tipo de movimentação inválido: " + request.getTipo());
        }
        return movimentacaoRepository.save(movimentacao);
    }


    // --- MÉTODO PRIVADO handleEntrada ---
    private void handleEntrada(MovimentacaoRequestDTO request, Jogo jogo, Plataforma plataforma, Movimentacao movimentacao) {
        if (request.getDepositoDestinoId() == null) {
            throw new IllegalArgumentException("Depósito de destino é obrigatório para entradas/ajustes positivos.");
        }
        Deposito deposito = depositoRepository.findById(request.getDepositoDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Depósito de destino não encontrado: " + request.getDepositoDestinoId()));
        movimentacao.setDepositoDestino(deposito);
        movimentacao.setDepositoOrigem(null);


        ItemEstoque item = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, deposito)
                .orElseGet(() -> {
                    ItemEstoque novoItem = new ItemEstoque();
                    novoItem.setJogo(jogo);
                    novoItem.setPlataforma(plataforma);
                    novoItem.setDeposito(deposito);
                    novoItem.setQuantidade(0);
                    novoItem.setPrecoUnitarioAtual(movimentacao.getPrecoUnitarioMomento());
                    return novoItem;
                });

        int quantidadeAnterior = item.getQuantidade();
        BigDecimal novoPrecoUnitario = item.getPrecoUnitarioAtual();

        if (request.getTipo() == TipoMovimentacao.ENTRADA && request.getPrecoUnitarioMomento() != null && quantidadeAnterior > 0) {
            BigDecimal valorTotalAnterior = item.getPrecoUnitarioAtual().multiply(new BigDecimal(quantidadeAnterior));
            BigDecimal valorNovaEntrada = request.getPrecoUnitarioMomento().multiply(new BigDecimal(request.getQuantidade()));
            BigDecimal novoValorTotalEstoque = valorTotalAnterior.add(valorNovaEntrada);
            novoPrecoUnitario = novoValorTotalEstoque.divide(new BigDecimal(quantidadeAnterior + request.getQuantidade()), 2, BigDecimal.ROUND_HALF_UP);
        } else if (quantidadeAnterior == 0 && request.getPrecoUnitarioMomento() != null) {
            novoPrecoUnitario = request.getPrecoUnitarioMomento();
        }


        item.setQuantidade(quantidadeAnterior + request.getQuantidade());
        item.setPrecoUnitarioAtual(novoPrecoUnitario);

        itemEstoqueRepository.save(item);
    }


    // --- MÉTODO PRIVADO handleSaida ---
    private void handleSaida(MovimentacaoRequestDTO request, Jogo jogo, Plataforma plataforma, Movimentacao movimentacao) {
        if (request.getDepositoOrigemId() == null) {
            throw new IllegalArgumentException("Depósito de origem é obrigatório para saídas/ajustes negativos.");
        }
        Deposito deposito = depositoRepository.findById(request.getDepositoOrigemId())
                .orElseThrow(() -> new ResourceNotFoundException("Depósito de origem não encontrado: " + request.getDepositoOrigemId()));
        movimentacao.setDepositoOrigem(deposito);
        movimentacao.setDepositoDestino(null);


        ItemEstoque item = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, deposito)
                .orElseThrow(() -> new ResourceNotFoundException("Item "+jogo.getTitulo()+"/"+plataforma.getNome()+" não encontrado no estoque '"+deposito.getNome()+"' para saída."));

        if (item.getQuantidade() < request.getQuantidade()) {
            throw new InsufficientStockException("Estoque insuficiente para " + jogo.getTitulo() + "/" + plataforma.getNome() + " no depósito '"+deposito.getNome()+"'. Solicitado: "+request.getQuantidade()+", Disponível: "+item.getQuantidade());
        }
        item.setQuantidade(item.getQuantidade() - request.getQuantidade());
        itemEstoqueRepository.save(item);
    }


    @Transactional
    public void registrarTransferencia(Long jogoId, Long plataformaId, Long depositoOrigemId, Long depositoDestinoId, int quantidade, String observacao) {
        Jogo jogo = jogoRepository.findById(jogoId).orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado: "+jogoId));
        Plataforma plataforma = plataformaRepository.findById(plataformaId).orElseThrow(() -> new ResourceNotFoundException("Plataforma não encontrada: "+plataformaId));
        Deposito depositoOrigem = depositoRepository.findById(depositoOrigemId).orElseThrow(() -> new ResourceNotFoundException("Depósito de origem não encontrado: "+depositoOrigemId));
        Deposito depositoDestino = depositoRepository.findById(depositoDestinoId).orElseThrow(() -> new ResourceNotFoundException("Depósito de destino não encontrado: "+depositoDestinoId));

        if (depositoOrigem.getId().equals(depositoDestino.getId())) {
            throw new IllegalArgumentException("Depósito de origem e destino não podem ser os mesmos.");
        }

        // SAÍDA DO DEPÓSITO DE ORIGEM
        ItemEstoque itemOrigem = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, depositoOrigem)
                .orElseThrow(() -> new ResourceNotFoundException("Item " + jogo.getTitulo() + "/" + plataforma.getNome() + " não encontrado no depósito de origem '" + depositoOrigem.getNome() + "' para transferência."));

        if (itemOrigem.getQuantidade() < quantidade) {
            throw new InsufficientStockException("Estoque insuficiente de "+ jogo.getTitulo() + "/" + plataforma.getNome() +" no depósito '"+depositoOrigem.getNome()+"' para transferência. Solicitado: "+quantidade+", Disponível: "+itemOrigem.getQuantidade());
        }
        itemOrigem.setQuantidade(itemOrigem.getQuantidade() - quantidade);
        itemEstoqueRepository.save(itemOrigem);

        // Registrar movimentação de SAÍDA pela transferência
        Movimentacao movSaida = new Movimentacao(null, LocalDateTime.now(), TipoMovimentacao.TRANSFERENCIA_SAIDA,
                jogo, plataforma, depositoOrigem, depositoDestino, // Destino aqui é para rastreio da transf.
                quantidade, itemOrigem.getPrecoUnitarioAtual(), // Usar preço do item em estoque na ORIGEM
                "Transferência para: " + depositoDestino.getNome() + (observacao != null ? " - " + observacao : ""));
        movimentacaoRepository.save(movSaida);

        // ENTRADA NO DEPÓSITO DE DESTINO
        // --- CORREÇÃO AQUI: Declarar a variável antes do orElseGet ---
        ItemEstoque itemDestino;
        itemDestino = itemEstoqueRepository.findByJogoAndPlataformaAndDeposito(jogo, plataforma, depositoDestino)
                .orElseGet(() -> {
                    ItemEstoque novoItem = new ItemEstoque();
                    novoItem.setJogo(jogo);
                    novoItem.setPlataforma(plataforma);
                    novoItem.setDeposito(depositoDestino);
                    novoItem.setQuantidade(0);
                    // Para transferências, o preço unitário geralmente é mantido o mesmo do item de origem
                    novoItem.setPrecoUnitarioAtual(itemOrigem.getPrecoUnitarioAtual());
                    return novoItem;
                });

        // O restante da lógica para atualizar itemDestino e registrar a movimentação de entrada permanece a mesma

        int quantidadeAnteriorDestino = itemDestino.getQuantidade(); // A quantidade ANTES de adicionar a quantidade da transferência
        BigDecimal novoPrecoUnitarioDestino = itemDestino.getPrecoUnitarioAtual();


        itemDestino.setQuantidade(quantidadeAnteriorDestino + quantidade); // Adiciona a quantidade transferida

        // Lógica de Custo Médio Ponderado para ENTRADAS (transferências) no DESTINO
        // Aplica CMP apenas se já havia itens no destino ANTES desta transferência
        if (quantidadeAnteriorDestino > 0) {
            BigDecimal valorTotalAnteriorDestino = itemDestino.getPrecoUnitarioAtual().multiply(new BigDecimal(quantidadeAnteriorDestino));
            BigDecimal valorNovaEntradaTransferencia = itemOrigem.getPrecoUnitarioAtual().multiply(new BigDecimal(quantidade)); // Valor da entrada da transferência (usando preço de origem)
            BigDecimal novoValorTotalEstoqueDestino = valorTotalAnteriorDestino.add(valorNovaEntradaTransferencia);
            // Cuidado com divisão por zero se a nova quantidade total for 0 (embora improvável após adicionar 'quantidade')
            if (itemDestino.getQuantidade() > 0) {
                novoPrecoUnitarioDestino = novoValorTotalEstoqueDestino.divide(new BigDecimal(itemDestino.getQuantidade()), 2, BigDecimal.ROUND_HALF_UP);
            } else {
                // Se a quantidade total for zero, o preço unitário pode ser zero ou o da entrada, dependendo da regra.
                // Para transferência, manter o preço de origem pode ser mais lógico aqui se a quantidade final for 0 (caso a transf. seja 0?)
                novoPrecoUnitarioDestino = itemOrigem.getPrecoUnitarioAtual(); // Ou BigDecimal.ZERO, dependendo da regra.
            }
        } else { // Primeiro item ou estoque anterior zero no destino
            novoPrecoUnitarioDestino = itemOrigem.getPrecoUnitarioAtual(); // Usa o preço do item de origem para a primeira entrada
        }
        itemDestino.setPrecoUnitarioAtual(novoPrecoUnitarioDestino); // Atualiza o preço unitário no destino

        itemEstoqueRepository.save(itemDestino);

        // Registrar movimentação de ENTRADA pela transferência
        Movimentacao movEntrada = new Movimentacao(null, LocalDateTime.now(), TipoMovimentacao.TRANSFERENCIA_ENTRADA,
                jogo, plataforma, depositoOrigem, // Origem aqui é para rastreio da transf.
                depositoDestino, quantidade, itemDestino.getPrecoUnitarioAtual(), // Usar o NOVO preço unitário do item no destino APÓS o CMP
                "Transferência de: " + depositoOrigem.getNome() + (observacao != null ? " - " + observacao : ""));
        movimentacaoRepository.save(movEntrada);
    }



    // --- MÉTODO CONSULTAR ESTOQUE RETORNANDO DTOs ---
    public List<ItemEstoqueDTO> consultarEstoque(Long jogoId, Long plataformaId, Long depositoId) {
        return itemEstoqueRepository.findWithDetailsByOptionalFilters(jogoId, plataformaId, depositoId);
    }

    // Este método continua usando entidades para calcular o valor total
    public BigDecimal calcularValorTotalEstoque(Long depositoId) {
        List<ItemEstoque> itens;
        if (depositoId != null) {
            Deposito deposito = depositoRepository.findById(depositoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Depósito não encontrado: " + depositoId));
            itens = itemEstoqueRepository.findByDeposito(deposito);
        } else {
            itens = itemEstoqueRepository.findAll();
        }

        return itens.stream()
                .map(item -> item.getPrecoUnitarioAtual().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
