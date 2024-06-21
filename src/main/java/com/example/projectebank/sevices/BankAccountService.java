package com.example.projectebank.sevices;

import com.example.projectebank.dtos.*;
import com.example.projectebank.exceptions.BankAccountNotFound;
import com.example.projectebank.exceptions.ClientNotFoundException;
import com.example.projectebank.exceptions.InsufficientBalanceException;

import java.util.List;

public interface BankAccountService {
    ClientDTO saveClient(ClientDTO clientDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long clientID) throws ClientNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long clientID) throws ClientNotFoundException;
    List<ClientDTO> listClients();
    BankAccountDTO getBankAccount(String accountID) throws BankAccountNotFound;
    void debit(String accountID, double amount, String description) throws BankAccountNotFound, InsufficientBalanceException;
    void credit(String accountID, double amount, String description) throws InsufficientBalanceException, BankAccountNotFound;
    void transfer(String fromAccountID, String toAccountID, double amount) throws InsufficientBalanceException, BankAccountNotFound;
    List<BankAccountDTO> listBankAccounts();

    ClientDTO getClientById(Long clientID) throws ClientNotFoundException;

    ClientDTO updateClient(ClientDTO clientDTO);

    void deleteClient(Long clientID);

    List<AccountOperationDTO> accountOperationHistory(String accountID);

    AccountHistoryDTO getAccountHistory(String accountID, int page, int size) throws BankAccountNotFound;

    List<ClientDTO> searchClients(String keyword);
}
