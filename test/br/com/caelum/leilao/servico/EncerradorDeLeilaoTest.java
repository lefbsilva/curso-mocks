package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de Plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();

		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}

	@Test
	public void naoDeveEncerrarLeiloesQueComecaramOntem() {
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DATE, -1);

		Calendar mesPassado = Calendar.getInstance();
		mesPassado.add(Calendar.MONTH, -1);

		Leilao leilaoQueComecouOntem = new CriadorDeLeilao().para("TV de Plasma").naData(ontem).constroi();
		Leilao leilaoQueComecouHa1Mes = new CriadorDeLeilao().para("Geladeira").naData(mesPassado).constroi();
		List<Leilao> leiloes = Arrays.asList(leilaoQueComecouOntem, leilaoQueComecouHa1Mes);

		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(leiloes);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();

		assertEquals(1, encerrador.getTotalEncerrados());
		assertFalse(leilaoQueComecouOntem.isEncerrado());
		assertTrue(leilaoQueComecouHa1Mes.isEncerrado());
	}

	@Test
	public void naoDeveFazerNadaSeNaoHouverLeiloes() {
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();

		assertEquals(0, encerrador.getTotalEncerrados());
	}

}