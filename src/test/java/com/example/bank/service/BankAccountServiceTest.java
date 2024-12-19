package com.example.bank.service;

import com.example.bank.exception.AccountNotFoundException;
import com.example.bank.exception.DuplicateAccountException;
import com.example.bank.model.BankAccount;
import com.example.bank.model.Client;
import com.example.bank.repository.BankAccountRepository;
import com.example.bank.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    @Mock
    private BankAccountRepository repository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private BankAccountService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBankAccount_shouldSaveAccount() {
        BankAccount account = new BankAccount(1L,
                new Client(10L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings");

        when(repository.findByAccountId(account.getAccountId())).thenReturn(Optional.empty());
        when(repository.save(account)).thenReturn(account);
        when(clientRepository.findById(account.getClient().getId())).thenReturn(Optional.of(account.getClient()));

        BankAccount result = service.createBankAccount(account);

        assertNotNull(result);
        assertEquals(account.getAccountId(), result.getAccountId());
        verify(repository).save(account);
    }

    @Test
    void createBankAccount_shouldThrowExceptionWhenDuplicateId() {
        BankAccount account = new BankAccount(1L,
                new Client(10L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings");

        when(repository.findByAccountId(account.getAccountId())).thenReturn(Optional.of(account));

        assertThrows(DuplicateAccountException.class, () -> service.createBankAccount(account));
        verify(repository, never()).save(any(BankAccount.class));
    }

    @Test
    void getAccountById_shouldReturnAccount() {
        BankAccount account = new BankAccount(1L,
                new Client(10L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings");


        when(repository.findByAccountId(account.getAccountId())).thenReturn(Optional.of(account));

        BankAccount result = service.getAccountById(account.getAccountId());

        assertNotNull(result);
        assertEquals(account.getAccountId(), result.getAccountId());
    }

    @Test
    void getAccountById_shouldThrowExceptionWhenAccountNotFound() {
        Long accountId = 1L;

        when(repository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.getAccountById(accountId));
    }

    @Test
    void getAllBankAccounts_shouldReturnListOfAccounts() {
        List<BankAccount> accounts = List.of(new BankAccount(1L,
                new Client(10L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings"),
                 new BankAccount(2L,
                         new Client(20L,
                                 "María",
                                 "maria@mail.com",
                                 11554433),
                         1000.0,
                         "Checking"));


        when(repository.findAll()).thenReturn(accounts);

        List<BankAccount> result = service.getAllBankAccounts();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void updateBankAccount_shouldUpdateAccount() {
        Long accountId = 1L;
        BankAccount existingAccount = new BankAccount(1L,
                new Client(10L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings");

        BankAccount updatedAccount = new BankAccount(1L,
                new Client(10L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings");

        updatedAccount.setBalance(200.0);

        when(repository.findByAccountId(accountId)).thenReturn(Optional.of(existingAccount));
        when(repository.save(existingAccount)).thenReturn(existingAccount);

        BankAccount result = service.updateBankAccount(accountId, updatedAccount);

        assertNotNull(result);
        assertEquals(200.0, result.getBalance());
        verify(repository).save(existingAccount);
    }

    @Test
    void updateBankAccount_shouldThrowExceptionWhenAccountNotFound() {
        Long accountId = 1L;
        BankAccount updatedAccount = new BankAccount(1L,
                new Client(30L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings");


        when(repository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.updateBankAccount(accountId, updatedAccount));
    }

    @Test
    void deleteBankAccount_shouldDeleteAccount() {
        Long accountId = 1L;

        BankAccount account = new BankAccount(1L,
                new Client(10L,
                        "Juan",
                        "juan@mail.com",
                        11223344),
                1000.0,
                "Savings");

        account.setAccountId(accountId);

        when(repository.findByAccountId(accountId)).thenReturn(Optional.of(account));

        service.deleteBankAccount(accountId);

        verify(repository).deleteById(accountId);
    }

    @Test
    void deleteBankAccount_shouldThrowExceptionWhenAccountNotFound() {
        Long accountId = 1L;

        when(repository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteBankAccount(accountId));
        verify(repository, never()).deleteById(accountId);
    }
}
