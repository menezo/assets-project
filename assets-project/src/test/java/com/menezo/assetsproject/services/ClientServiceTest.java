package com.menezo.assetsproject.services;

import com.menezo.assetsproject.model.entities.*;
import com.menezo.assetsproject.model.repository.ClientRepository;
import com.menezo.assetsproject.model.services.ClientService;
import com.menezo.assetsproject.model.services.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClientGetById() {
        Client client1 = new Client("Joao");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client1));

        Optional<Client> result = clientService.getClientById(1L);
        assertTrue(result.isPresent());
        assertEquals("Joao", result.get().getName());
    }

    @Test
    public void testGetClientByIdNotFound() {
        when(clientRepository.findById(100L)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.getClientById(100L);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetClientsByName() {
        List<Client> clients = new ArrayList<>();
        clients.add(new Client("Maria"));
        when(clientRepository.findByNameContainingIgnoreCase("Ma")).thenReturn(clients);

        List<Client> resultClients = clientService.getClientsByName("Ma");
        assertFalse(resultClients.isEmpty());

        for (Client client : resultClients) {
            assertTrue(client.getName().contains("Ma"));
        }
    }

    @Test
    public void testGetClientsByNameNotFound() {
        when(clientRepository.findByNameContainingIgnoreCase("Ronaldinho")).thenReturn(new ArrayList<>());

        List<Client> clients = clientService.getClientsByName("Ronaldinho");
        assertTrue(clients.isEmpty());
    }

    @Test
    public void testGetPortfoliosByClientId() {
        Client client = new Client("Joao");
        Portfolio portfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        client.addPortfolio(portfolio);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        List<Portfolio> result = clientService.getPortfoliosByClientId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());

        Portfolio resultPortfolio = result.get(0);
        assertEquals(PortfolioType.STOCKS, resultPortfolio.getType());
        assertEquals(0, resultPortfolio.getAssets().size());
    }

    @Test
    public void testGetPortfoliosByClientIdNotFound() {

        when(clientRepository.findById(100L)).thenReturn(Optional.empty());

        List<Portfolio> result = clientService.getPortfoliosByClientId(100L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateClient() {
        Client client = new Client("Carlos");
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client createdClient = clientService.createClient(client);
        assertNotNull(createdClient);
        assertEquals("Carlos", createdClient.getName());
    }

    @Test
    public void testCreateClientWithInvalidData() {

        Client client = new Client(null);
        assertThrows(IllegalArgumentException.class, () -> clientService.createClient(client));
    }

    @Test
    public void testUpdateClient() {
        Long clientId = 1L;
        Client existingClient = new Client("Joao");

        ReflectionTestUtils.setField(existingClient, "id", clientId);

        Client updatedClientInfo = new Client("Joao Vitor");
        List<Portfolio> updatedPortfolios = new ArrayList<>();
        updatedPortfolios.add(new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0));
        updatedClientInfo.setPortfolios(updatedPortfolios);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client result = clientService.updateClient(clientId, updatedClientInfo);

        assertNotNull(result);
        assertEquals("Joao Vitor", result.getName());
        assertEquals(clientId, result.getId());
        assertEquals(1, result.getPortfolios().size());
        assertEquals(PortfolioType.STOCKS, result.getPortfolios().get(0).getType());

        verify(clientRepository).findById(clientId);
        verify(clientRepository).save(existingClient);
    }

    @Test
    public void testUpdateClientWithInvalidData() {
        Long clientId = 100L;
        Client updatedClientInfo = new Client("Joao Vitor");

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Client result = clientService.updateClient(clientId, updatedClientInfo);

        assertNull(result);
        verify(clientRepository).findById(clientId);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testUpdateClientException() {
        Long clientId = 1L;
        Client updatedClientInfo = new Client("Joao Vitor");

        when(clientRepository.findById(clientId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> clientService.updateClient(clientId, updatedClientInfo));
        verify(clientRepository).findById(clientId);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testDeleteClient() {
        Long clientId = 1L;
        Client existingClient = new Client("Joao");

        ReflectionTestUtils.setField(existingClient, "id", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientRepository).delete(any(Client.class));

        boolean result = clientService.deleteClient(clientId);

        assertTrue(result);
        verify(clientRepository).findById(clientId);
        verify(clientRepository).delete(existingClient);
    }

    @Test
    public void testDeleteClientNotFound() {
        Long clientId = 100L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        boolean result = clientService.deleteClient(clientId);

        assertFalse(result);
        verify(clientRepository).findById(clientId);
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    public void testDeleteClientException() {
        Long clientId = 1L;
        Client existingClient = new Client("Joao");
        ReflectionTestUtils.setField(existingClient, "id", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        doThrow(new RuntimeException("Database error")).when(clientRepository).delete(existingClient);

        assertThrows(RuntimeException.class, () -> clientService.deleteClient(clientId));
        verify(clientRepository).findById(clientId);
        verify(clientRepository).delete(existingClient);
    }

    @Test
    public void testAddPortfolioToClient() {
        Long clientId = 1L;
        Client existingClient = new Client("Joao");
        existingClient.setPortfolios(new ArrayList<>());

        Portfolio portfolioToAdd = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        Portfolio savedPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.createPortfolio(portfolioToAdd)).thenReturn(savedPortfolio);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Client> result = clientService.addPortfolioToClient(clientId, portfolioToAdd);

        assertTrue(result.isPresent());
        assertEquals(existingClient, result.get());
        assertEquals(1, result.get().getPortfolios().size());
        assertEquals(savedPortfolio, result.get().getPortfolios().get(0));

        verify(clientRepository).findById(clientId);
        verify(portfolioService).createPortfolio(portfolioToAdd);
        verify(clientRepository).save(existingClient);
    }

    @Test
    public void testAddPortfolioToClient_ClientNotFound() {
        Long clientId = 100L;
        Portfolio portfolioToAdd = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.addPortfolioToClient(clientId, portfolioToAdd);

        assertFalse(result.isPresent());
        verify(clientRepository).findById(clientId);
        verify(portfolioService, never()).createPortfolio(any(Portfolio.class));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testAddPortfolioToClient_Exception() {
        Long clientId = 1L;
        Client existingClient = new Client("Joao");
        existingClient.setPortfolios(new ArrayList<>());

        Portfolio portfolioToAdd = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.createPortfolio(portfolioToAdd)).thenThrow(new RuntimeException("Error creating portfolio"));

        assertThrows(RuntimeException.class, () -> clientService.addPortfolioToClient(clientId, portfolioToAdd));

        verify(clientRepository).findById(clientId);
        verify(portfolioService).createPortfolio(portfolioToAdd);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testUpdatePortfolioForClient() {
        Long clientId = 1L;
        Long portfolioId = 2L;
        Client existingClient = new Client("Joao");
        Portfolio existingPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        existingClient.getPortfolios().add(existingPortfolio);

        Portfolio updatedPortfolio = new Portfolio(PortfolioType.REITS, new ArrayList<>(), 20.0);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.of(existingPortfolio));
        when(portfolioService.updatePortfolio(eq(portfolioId), any(Portfolio.class))).thenReturn(updatedPortfolio);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Client> result = clientService.updatePortfolioForClient(clientId, portfolioId, updatedPortfolio);

        assertTrue(result.isPresent());
        assertEquals(existingClient, result.get());
        assertEquals(1, result.get().getPortfolios().size());
        assertEquals(updatedPortfolio.getType(), result.get().getPortfolios().get(0).getType());
        assertEquals(updatedPortfolio.getCurrentWeight(), result.get().getPortfolios().get(0).getCurrentWeight());

        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService).updatePortfolio(portfolioId, existingPortfolio);
        verify(clientRepository).save(existingClient);
    }

    @Test
    public void testUpdatePortfolioForClient_ClientNotFound() {
        Long clientId = 100L;
        Long portfolioId = 2L;
        Portfolio updatedPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 20.0);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.updatePortfolioForClient(clientId, portfolioId, updatedPortfolio);

        assertFalse(result.isPresent());
        verify(clientRepository).findById(clientId);
        verify(portfolioService, never()).getPortfolioById(anyLong());
        verify(portfolioService, never()).updatePortfolio(anyLong(), any(Portfolio.class));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testUpdatePortfolioForClient_PortfolioNotFound() {
        Long clientId = 1L;
        Long portfolioId = 100L;
        Client existingClient = new Client("Joao");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.updatePortfolioForClient(clientId, portfolioId, new Portfolio());

        assertFalse(result.isPresent());
        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService, never()).updatePortfolio(anyLong(), any(Portfolio.class));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testUpdatePortfolioForClient_Exception() {
        Long clientId = 1L;
        Long portfolioId = 2L;
        Client existingClient = new Client("Joao");
        Portfolio existingPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        existingClient.getPortfolios().add(existingPortfolio);

        Portfolio updatedPortfolio = new Portfolio(PortfolioType.REITS, new ArrayList<>(), 20.0);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.of(existingPortfolio));
        when(portfolioService.updatePortfolio(eq(portfolioId), any(Portfolio.class))).thenThrow(new RuntimeException("Error updating portfolio"));

        assertThrows(RuntimeException.class, () -> clientService.updatePortfolioForClient(clientId, portfolioId, updatedPortfolio));

        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService).updatePortfolio(portfolioId, existingPortfolio);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testDeletePortfolioFromClient() {
        Long clientId = 1L;
        Long portfolioId = 2L;
        Client existingClient = new Client("Joao");
        ReflectionTestUtils.setField(existingClient, "id", clientId);

        Portfolio existingPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        ReflectionTestUtils.setField(existingPortfolio, "id", portfolioId);
        existingClient.getPortfolios().add(existingPortfolio);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.of(existingPortfolio));
        when(portfolioService.deletePortfolio(portfolioId)).thenReturn(true);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Client> result = clientService.deletePortfolioFromClient(clientId, portfolioId);

        assertTrue(result.isPresent());
        assertEquals(existingClient, result.get());
        assertFalse(result.get().getPortfolios().contains(existingPortfolio));

        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService).deletePortfolio(portfolioId);
        verify(clientRepository).save(existingClient);
    }

    @Test
    public void testDeletePortfolioFromClient_ClientNotFound() {
        Long clientId = 100L;
        Long portfolioId = 2L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.deletePortfolioFromClient(clientId, portfolioId);

        assertFalse(result.isPresent());
        verify(clientRepository).findById(clientId);
        verify(portfolioService, never()).getPortfolioById(anyLong());
        verify(portfolioService, never()).deletePortfolio(anyLong());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testDeletePortfolioFromClient_PortfolioNotFound() {
        Long clientId = 1L;
        Long portfolioId = 100L;
        Client existingClient = new Client("Joao");
        Portfolio existingPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        existingClient.getPortfolios().add(existingPortfolio);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.deletePortfolioFromClient(clientId, portfolioId);

        assertFalse(result.isPresent());
        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService, never()).deletePortfolio(anyLong());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testDeletePortfolioFromClient_PortfolioNotOwnedByClient() {
        Long clientId = 1L;
        Long portfolioId = 2L;
        Client existingClient = new Client("Joao");
        ReflectionTestUtils.setField(existingClient, "id", clientId);

        Portfolio anotherPortfolio = new Portfolio(PortfolioType.REITS, new ArrayList<>(), 15.0);
        ReflectionTestUtils.setField(anotherPortfolio, "id", portfolioId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.of(anotherPortfolio));

        Optional<Client> result = clientService.deletePortfolioFromClient(clientId, portfolioId);

        assertFalse(result.isPresent());

        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService, never()).deletePortfolio(anyLong());
        verify(clientRepository, never()).save(any(Client.class));
    }


    @Test
    public void testDeletePortfolioFromClient_Exception() {
        Long clientId = 1L;
        Long portfolioId = 2L;
        Client existingClient = new Client("Joao");
        ReflectionTestUtils.setField(existingClient, "id", clientId);

        Portfolio existingPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        ReflectionTestUtils.setField(existingPortfolio, "id", portfolioId);
        existingClient.getPortfolios().add(existingPortfolio);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.of(existingPortfolio));
        when(portfolioService.deletePortfolio(portfolioId)).thenThrow(new RuntimeException("Error deleting portfolio"));

        assertThrows(RuntimeException.class, () -> clientService.deletePortfolioFromClient(clientId, portfolioId));

        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService).deletePortfolio(portfolioId);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testDeleteLastPortfolioFromClient() {
        Long clientId = 1L;
        Long portfolioId = 2L;
        Client existingClient = new Client("Joao");
        ReflectionTestUtils.setField(existingClient, "id", clientId);

        Portfolio existingPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);
        ReflectionTestUtils.setField(existingPortfolio, "id", portfolioId);
        existingClient.getPortfolios().add(existingPortfolio);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(portfolioService.getPortfolioById(portfolioId)).thenReturn(Optional.of(existingPortfolio));
        when(portfolioService.deletePortfolio(portfolioId)).thenReturn(true);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Client> result = clientService.deletePortfolioFromClient(clientId, portfolioId);

        assertTrue(result.isPresent());
        assertEquals(existingClient, result.get());
        assertTrue(result.get().getPortfolios().isEmpty());

        verify(clientRepository).findById(clientId);
        verify(portfolioService).getPortfolioById(portfolioId);
        verify(portfolioService).deletePortfolio(portfolioId);
        verify(clientRepository).save(existingClient);
    }

    @Test
    public void testDeletePortfolioFromClient_NullClientId() {
        Long portfolioId = 2L;

        assertThrows(NullPointerException.class, () -> clientService.deletePortfolioFromClient(null, portfolioId));
        verify(clientRepository, never()).findById(anyLong());
        verify(portfolioService, never()).getPortfolioById(anyLong());
        verify(portfolioService, never()).deletePortfolio(anyLong());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testDeletePortfolioFromClient_NullPortfolioId() {
        Long clientId = 1L;

        assertThrows(NullPointerException.class, () -> clientService.deletePortfolioFromClient(clientId, null));
        verify(clientRepository, never()).findById(anyLong());
        verify(portfolioService, never()).getPortfolioById(anyLong());
        verify(portfolioService, never()).deletePortfolio(anyLong());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testGetAllClients_Success() {
        List<Client> expectedClients = Arrays.asList(
                new Client("John Doe"),
                new Client("Jane Doe")
        );
        when(clientRepository.findAll()).thenReturn(expectedClients);

        List<Client> actualClients = clientService.getAllClients();

        assertNotNull(actualClients);
        assertEquals(expectedClients, actualClients);
        verify(clientRepository).findAll();
    }

    @Test
    public void testGetAllClients_RepositoryReturnsEmptyList() {
        List<Client> expectedClients = Collections.emptyList();
        when(clientRepository.findAll()).thenReturn(expectedClients);

        List<Client> actualClients = clientService.getAllClients();

        assertNotNull(actualClients);
        assertTrue(actualClients.isEmpty());
        assertEquals(expectedClients, actualClients);
        verify(clientRepository).findAll();
    }

    @Test
    public void testGetAllClients_RepositoryThrowsException() {
        Exception expectedException = new RuntimeException("Mocked repository exception");
        when(clientRepository.findAll()).thenThrow(expectedException);

        assertThrows(RuntimeException.class, () -> clientService.getAllClients());
        verify(clientRepository).findAll();
    }

    @Test
    public void testGetAllClients_ServiceLogsAndRethrowsException() {
        Exception expectedException = new RuntimeException("Mocked repository exception");
        when(clientRepository.findAll()).thenThrow(expectedException);

        assertThrows(RuntimeException.class, () -> clientService.getAllClients());
        verify(clientRepository).findAll();
    }

}
