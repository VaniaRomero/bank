package com.example.bank.controller;

import com.example.bank.model.BankAccount;
import com.example.bank.service.BankAccountService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {

    private final BankAccountService service;

    public BankAccountController(BankAccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<BankAccount> getAllBankAccounts() {
        return service.getAllBankAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getBankAccountById(@PathVariable Long id) {
        Optional<BankAccount> account = Optional.ofNullable(service.getAccountById(id));
        if (account.isPresent()) {
            return ResponseEntity.ok(account.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount bankAccount) {
        BankAccount createdAccount = service.createBankAccount(bankAccount);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccount> updateBankAccount(@PathVariable Long id, @RequestBody BankAccount updatedAccount) {
        BankAccount account = service.updateBankAccount(id, updatedAccount);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long id) {
        service.deleteBankAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/consume")
    public List<BankAccount> consumeSelf() {
        String url = "http://localhost:8080/accounts";
        return new RestTemplate().exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BankAccount>>() {}
        ).getBody();
    }

}
