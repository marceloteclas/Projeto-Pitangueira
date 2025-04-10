package com.example.Projeto.de.Analise.de.Inseguranca.Alimentar.repository;

import com.example.Projeto.de.Analise.de.Inseguranca.Alimentar.model.Avaliacao;
import com.example.Projeto.de.Analise.de.Inseguranca.Alimentar.model.User;
import com.example.Projeto.de.Analise.de.Inseguranca.Alimentar.common.enums.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    // Consultas customizadas para relatórios

    @Query("SELECT AVG(q.idade) FROM QuestionarioSocioeconomico q")
    Double calcularMediaIdade();

    @Query("SELECT COUNT(a) FROM Avaliacao a " +
            "WHERE a.marcadoresConsumo.consumoDiaAnterior.feijao = true")
    Long countByConsumoFeijao();

    @Query("SELECT NEW map(a.ebia.respostas as respostas, COUNT(a) as total) " +
            "FROM Avaliacao a GROUP BY a.ebia.respostas")
    List<Map<String, Object>> agregarRespostasEBIA();

    @Query("SELECT (COUNT(a) * 100.0 / (SELECT COUNT(a) FROM Avaliacao a)) " +
            "FROM Avaliacao a WHERE SIZE(a.ebia.respostas) > 2")
    Double calcularInsegurancaAlimentar();

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.marcadoresConsumo.consumoDiaAnterior.feijao = true")
    Long countByConsumoFeijao(boolean consumiu);

    @Query("SELECT NEW map(q.genero as chave, COUNT(q) as valor) " +
            "FROM QuestionarioSocioeconomico q GROUP BY q.genero")
    Map<Genero, Long> contarPorGenero();

    @Query("SELECT CASE " +
            "WHEN SIZE(a.ebia.respostas) > 4 THEN 'INSEGURANÇA_GRAVE' " +
            "WHEN SIZE(a.ebia.respostas) > 2 THEN 'INSEGURANÇA_MODERADA' " +
            "ELSE 'SEGURANÇA_ALIMENTAR' END as classificacao, " +
            "COUNT(a) as total " +
            "FROM Avaliacao a " +
            "GROUP BY classificacao")
    List<Map<String, Long>> classificarInsegurancaAlimentar();

    List<Avaliacao> findByUser(User user);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.user = :user AND a.concluida = false")
    Long countByUserAndConcluidaFalse(@Param("user") User user);

    @Query("SELECT a FROM Avaliacao a WHERE a.user = :user ORDER BY a.dataCadastro DESC LIMIT 1")
    Optional<Avaliacao> findTopByUserOrderByDataCadastroDesc(@Param("user") User user);
}