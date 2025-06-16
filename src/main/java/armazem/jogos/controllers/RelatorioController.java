package armazem.jogos.controllers;

import armazem.jogos.dtos.RelatorioEstoqueConsolidadoDTO;
import armazem.jogos.dtos.RelatorioMovimentacaoDTO;
import armazem.jogos.entities.TipoMovimentacao;
import armazem.jogos.services.RelatorioService;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://majestic-lebkuchen-9305f3.netlify.app",
        "front-jogos.vercel.app"
})
public class RelatorioController {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioController.class);

    @Autowired
    RelatorioService relatorioService;

    @GetMapping("/movimentacoes/json") // Endpoint para JSON
    public ResponseEntity<List<RelatorioMovimentacaoDTO>> getRelatorioMovimentacoesJson(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) TipoMovimentacao tipo,
            @RequestParam(required = false) Long jogoId,
            @RequestParam(required = false) Long plataformaId,
            @RequestParam(required = false) Long depositoId
    ) {
        logger.info("Recebida requisição para relatório de movimentações JSON com parâmetros: dataInicio={}, dataFim={}, tipo={}, jogoId={}, plataformaId={}, depositoId={}",
                dataInicio, dataFim, tipo, jogoId, plataformaId, depositoId);
        List<RelatorioMovimentacaoDTO> relatorio = relatorioService.gerarRelatorioMovimentacoesJson(
                dataInicio, dataFim, tipo, jogoId, plataformaId, depositoId
        );
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/movimentacoes/pdf") // Endpoint para PDF
    public ResponseEntity<byte[]> getRelatorioMovimentacoesPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) TipoMovimentacao tipo,
            @RequestParam(required = false) Long jogoId,
            @RequestParam(required = false) Long plataformaId,
            @RequestParam(required = false) Long depositoId
    ) {
        logger.info("Recebida requisição para relatório de movimentações PDF com parâmetros: dataInicio={}, dataFim={}, tipo={}, jogoId={}, plataformaId={}, depositoId={}",
                dataInicio, dataFim, tipo, jogoId, plataformaId, depositoId);
        try {
            byte[] pdfBytes = relatorioService.gerarRelatorioMovimentacoesPdf(
                    dataInicio, dataFim, tipo, jogoId, plataformaId, depositoId
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_movimentacoes.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (JRException e) {
            logger.error("Erro ao gerar relatório PDF de movimentações (JRException): {}", e.getMessage(), e);
            return new ResponseEntity<>(("Erro ao gerar PDF: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar relatório PDF de movimentações: {}", e.getMessage(), e);
            return new ResponseEntity<>(("Erro inesperado: " + e.getMessage()).getBytes(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/estoque") // Endpoint JSON para estoque
    public ResponseEntity<RelatorioEstoqueConsolidadoDTO> getRelatorioEstoque(
            @RequestParam(required = false) Long jogoId,
            @RequestParam(required = false) Long plataformaId,
            @RequestParam(required = false) Long depositoId
    ) {
        logger.info("Recebida requisição para relatório de estoque com parâmetros: jogoId={}, plataformaId={}, depositoId={}",
                jogoId, plataformaId, depositoId);
        RelatorioEstoqueConsolidadoDTO relatorio = relatorioService.gerarRelatorioEstoqueAtual(jogoId, plataformaId, depositoId);
        return ResponseEntity.ok(relatorio);
    }
}