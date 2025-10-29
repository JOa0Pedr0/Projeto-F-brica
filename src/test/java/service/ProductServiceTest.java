
package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.MachineDAO;
import dao.ProductDAO;
import entities.Machine;
import entities.Product;
import entities.StatusMachine;
import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@Mock
	private MachineDAO machineDAOMock;

	@Mock
	private ProductDAO productDAOMock;

	@InjectMocks
	private ProductService productService;

	@Test
	@DisplayName("Deve lançar BusinessRuleException ao tentar adicionar produto com preço menor ou igual a zero")
	public void deveLancarExcecaoAoAdicionarProdutoComPrecoZeroOuNegativo() {

		String nome = "Parafuso Teste";
		String descricao = "Descrição de teste";
		double precoInvalido = 0.0;
		int maquinaId = 1;

		Machine maquinaFalsa = new Machine("Prensa Mock", StatusMachine.OPERANDO);

		when(machineDAOMock.buscarPorId(maquinaId)).thenReturn(maquinaFalsa);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			productService.adicionarProduto(nome, descricao, precoInvalido, maquinaId);
		});

		String expectedMessage = "O preço informado deve ser maior que 0.";
		assertEquals(expectedMessage, exception.getMessage());
	}

	@Test
	@DisplayName("Deve cadastrar produto com sucesso quando os dados são válidos")
	public void deveCadastrarProdutoComSucesso() {

		String nome = "Porca Teste";
		String descricao = "Desc";
		double precoValido = 1.50;
		int maquinaId = 2;
		Machine maquinaFalsa = new Machine("Torno Mock", StatusMachine.OPERANDO);

		when(machineDAOMock.buscarPorId(maquinaId)).thenReturn(maquinaFalsa);

		productService.adicionarProduto(nome, descricao, precoValido, maquinaId);

	}

	@Test
	@DisplayName("Deve lançar ResourceNotFoundException ao tentar adicionar produto com ID de máquina inexistente")
	public void deveLancarExcecoAoAdicionarProdutoComMaquinaInexistente() {

		String nome = "Produto com Maquina null";
		String descricao = "Teste";
		double precoValido = 10.0;
		int maquinaInvalida = 999;

		when(machineDAOMock.buscarPorId(maquinaInvalida))
				.thenThrow(new ResourceNotFoundException("Máquina não encontrada para o ID: " + maquinaInvalida));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> productService.adicionarProduto(nome, descricao, precoValido, maquinaInvalida));

		assertEquals("Máquina não encontrada para o ID: " + maquinaInvalida, exception.getMessage());

	}

	@Test
	@DisplayName("Deve atualizar o produto com sucesso quando os dados são válido")
	public void deveAtualizarProdutoComSucesso() {
		int produtoIdExistente = 1;
		String novoNome = "Produto Atualizado";
		String novaDescricao = "Desc Atualizada";
		double novoPreco = 25.90;
		int novaMaquinaId = 2;

		Machine maquinaOriginal = new Machine("Prensa Original", StatusMachine.OPERANDO);
		Product produtoOriginal = new Product(10.0, "Produto Original", "Desc Original", maquinaOriginal);
		Machine novaMaquina = new Machine("Outra Máquina", StatusMachine.OPERANDO);

		when(productDAOMock.buscarPorId(produtoIdExistente)).thenReturn(produtoOriginal);
		when(machineDAOMock.buscarPorId(novaMaquinaId)).thenReturn(novaMaquina);

		productService.atualizar(produtoIdExistente, novoNome, novaDescricao, novoPreco, novaMaquinaId);

		ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

		verify(productDAOMock, times(1)).atualizar(productCaptor.capture());

		Product produtoAtualizadoPeloServico = productCaptor.getValue();

		assertEquals(novoNome, produtoAtualizadoPeloServico.getNome());
		assertEquals(novaDescricao, produtoAtualizadoPeloServico.getDescricao());
		assertEquals(novoPreco, produtoAtualizadoPeloServico.getPrecoCusto());
		assertEquals(novaMaquina, produtoAtualizadoPeloServico.getMaquina());

	}

	@Test
	@DisplayName("Deve lançar BusinessRuleException ao tentar atualizar produto com preço inválido")
	public void deveLancarExcecaoAoAtualizarProdutoComPrecoInvalido() {
		int produtoExistente = 1;
		double precoInvalido = -10.0;
		int maquinaValida = 1;

		Machine maquinaOriginal = new Machine("Prensa Original", StatusMachine.OPERANDO);
		Product produtoOriginal = new Product(10.0, "Origional", "", maquinaOriginal);

		when(productDAOMock.buscarPorId(produtoExistente)).thenReturn(produtoOriginal);
		when(machineDAOMock.buscarPorId(maquinaValida)).thenReturn(maquinaOriginal);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			productService.atualizar(produtoExistente, "Novo nome", "Desc Nova", precoInvalido, maquinaValida);
		});
		assertEquals("O preço informado deve ser maior que 0.", exception.getMessage());
	}

	@Test
	@DisplayName("Deve remover o produto com sucesso")
	public void deveRemoverProdutoComSucesso() {
		int produtoIdParaRemover = 5;

		Machine maquinaTeste = new Machine("Teste", StatusMachine.PARADA);

		Product produtoTeste = new Product(5, "Produto Teste", "Desc Teste", maquinaTeste);

		when(productDAOMock.buscarPorId(produtoIdParaRemover)).thenReturn(produtoTeste);

		productService.remover(produtoIdParaRemover);

		verify(productDAOMock, times(1)).remover(produtoTeste);
	}

	@Test
	@DisplayName("Deve lançar execeção ao tentar remover produto com ID inexistente.")
	public void deveLancarExcecaoAoRemoverProdutoInvalido() {
		int produtoInexistente = 999;

		when(productDAOMock.buscarPorId(produtoInexistente))
				.thenThrow(new ResourceNotFoundException("Produto não encontrado para o ID: " + produtoInexistente));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> productService.remover(produtoInexistente));

		assertEquals("Produto não encontrado para o ID: " + produtoInexistente, exception.getMessage());

	}
}