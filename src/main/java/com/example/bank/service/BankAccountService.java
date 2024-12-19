package com.example.bank.service;

import com.example.bank.exception.AccountNotFoundException;
import com.example.bank.exception.DuplicateAccountException;
import com.example.bank.model.BankAccount;
import com.example.bank.model.Client;
import com.example.bank.repository.BankAccountRepository;
import com.example.bank.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {

    private final BankAccountRepository repository;

    private final ClientRepository clientRepository;

    public BankAccountService(BankAccountRepository repository, ClientRepository clientRepository) {
        this.repository = repository;
        this.clientRepository = clientRepository;
    }

    public BankAccount createBankAccount(BankAccount account) {
        if (repository.findByAccountId(account.getAccountId()).isPresent()) {
            throw new DuplicateAccountException("This account id already exists.");
        }

        if (account.getClient() == null || account.getClient().getId() == null) {
            throw new IllegalArgumentException("Client information is missing.");
        }

        Optional<Client> existingClient = clientRepository.findById(account.getClient().getId());
        if (existingClient.isEmpty()) {
            throw new IllegalArgumentException("Client ID " + account.getClient().getId() + " does not exist.");
        }

        account.setClient(existingClient.get());

        return repository.save(account);
    }

    public BankAccount getAccountById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Account id cannot be null.");
        }
        return repository.findByAccountId(id)
                .orElseThrow(() -> new AccountNotFoundException("Account id " + id + " does not exist."));
    }

    public List<BankAccount> getAllBankAccounts() {
        return repository.findAll();
    }

    public BankAccount updateBankAccount(Long id, BankAccount updatedAccount) {
        BankAccount existingAccount = repository.findByAccountId(id)
                .orElseThrow(() -> new AccountNotFoundException("The account could not be found."));

        if (updatedAccount.getBalance() != null) {
            existingAccount.setBalance(updatedAccount.getBalance());
        }

        return repository.save(existingAccount);
    }

    public void deleteBankAccount(Long id) {
        if (repository.findByAccountId(id).isEmpty()) {
            throw new IllegalArgumentException("Cannot delete. Account id " + id + " does not exist.");
        }
        repository.deleteById(id);

    }

}
