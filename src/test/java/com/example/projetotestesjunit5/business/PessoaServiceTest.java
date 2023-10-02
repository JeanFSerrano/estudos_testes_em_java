package com.example.projetotestesjunit5.business;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static java.lang.String.format;

import com.example.projetotestesjunit5.infrastructure.PessoaRepository;
import com.example.projetotestesjunit5.infrastructure.entity.Pessoa;
import com.example.projetotestesjunit5.infrastructure.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

    // Classe que está sendo testada
    @InjectMocks
    PessoaService service;

    // Classe que a classe que está sendo testada usa
    @Mock
    PessoaRepository repository;

    // Entidade que é usada de retorno nos métodos
    Pessoa pessoa;

    // Usado para sinalizar dados adiconados dados(Mock) antes de começar os testes
    @BeforeEach
    public void setUp() {
        pessoa = new Pessoa("jean", "123456789", "dev", 20, "texeira", "talvez", 9);

    }

    // Testando os métodos e procedimentos que utilizamos nas classes
    @Test
    void shouldGetPessoasByCpfWithSuccess() {

        when(repository
        .findPessoa(pessoa.getCpf()))
        // Mockando pessoa para uma lista de pessoas que é o que o método deve retornar
        .thenReturn(Collections.singletonList(pessoa));


        // Consultando o método e salva numa variável para fazer o teste de tipo
        List<Pessoa> pessoas = service
        .buscaPessoasPorCpf(pessoa.getCpf());


        // Compara se o método retorna o mesmo tipo de dado esperado
        assertEquals(Collections.singletonList(pessoa), pessoas);
        // Verifica se o repository é chamado uma vez
        verify(repository).findPessoa(pessoa.getCpf());
        // Verificar se depois de executar o método o repository não é chamado mais vezes
        verifyNoMoreInteractions(repository);
    }

    // Verifica se a Exception é chamada quando o CPF é nulo
    @Test
    void shouldNotCallCpfParameterCaseItsNull() {

        // Verificando se a mensagem de null é a especifacada na classe
        final BusinessException e = assertThrows(BusinessException.class,
                () -> service.buscaPessoasPorCpf(null));

        // Verificando se a mensagem de expection é um valor não nulo
        assertThat(e, notNullValue());

        // Verificando se a mensagem de retorno é o que foi definido no método
        assertThat(e.getMessage(), is("Erro ao buscar pessoas por cpf = null"));

        // Verificando se a causa da mensagem é porque o parametro not null foi ativado
        assertThat(e.getCause(), notNullValue());

        // Verificando se a causa da mensagem é o valor da mensagem definiada no método
        assertThat(e.getCause().getMessage(), is("Cpf é obrigatório!"));

        // Ao dar mensagem de not null o repository não deve chamado, então isso também
        // é verificado
        verifyNoInteractions(repository);

    }

    // Verifica se as mensagens de Expetion são funcionando corretamente quando o
    // repository falha
    @Test
    void shouldActivateExceptionWhenRepositoryFails() {
        
        // Simulando a falha do repository ao consultar pessoa
        when(repository.findPessoa(pessoa.getCpf()))
        .thenThrow(new RuntimeException("Falhar ao buscar por cpf"));

        // Simulando é a causa da mensagem de erro no repository é uma Business Exception
        final BusinessException e = assertThrows(BusinessException.class, 
            () -> service.buscaPessoasPorCpf(pessoa.getCpf()));


        // Verificando se a messagem de erro quando o repository falha é o que definimos no método    
        assertThat(e.getMessage(), is(format("Erro ao buscar pessoas por cpf = %s", pessoa.getCpf())));

        // Verificando se o erro é de origem da classe RuntimeException
        assertThat(e.getCause().getClass(), is(RuntimeException.class));

        // Verificando se a mensagem de erro quando o repository falha é que estipulamos para o retorno
        assertThat(e.getCause().getMessage(), is("Falhar ao buscar por cpf"));

        // Verificando se o repository foi chamado
        verify(repository).findPessoa(pessoa.getCpf());

        // Verificando se não tivemos mais interações com o repository
        verifyNoMoreInteractions(repository);
    }
}
