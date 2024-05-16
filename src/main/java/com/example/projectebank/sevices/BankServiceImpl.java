package com.example.projectebank.sevices;

import com.example.projectebank.dtos.*;
import com.example.projectebank.entities.*;
import com.example.projectebank.enums.OperationType;
import com.example.projectebank.exceptions.BankAccountNotFound;
import com.example.projectebank.exceptions.ClientNotFoundException;
import com.example.projectebank.exceptions.InsufficientBalanceException;
import com.example.projectebank.mappers.BankAccountMapperImpl;
import com.example.projectebank.repositories.AccountOperationRepository;
import com.example.projectebank.repositories.BankAccountRepository;
import com.example.projectebank.repositories.ClientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @Transactional @AllArgsConstructor @Slf4j
public class BankServiceImpl implements BankAccountService {
    private BankAccountRepository bankAccountRepository;
    private ClientRepository clientRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl bankAccountMapper;

    @Override
    public ClientDTO saveClient(ClientDTO clientDTO) {
        log.info("Saving new client");
        Client client = bankAccountMapper.fromClientDTO(clientDTO);
        Client savedClient = clientRepository.save(client);
        return bankAccountMapper.fromClient(savedClient);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long clientID) throws ClientNotFoundException {
        Client client = clientRepository.findById(clientID).orElse(null);
        if (client == null) {
            throw new ClientNotFoundException("Client not found");
        }

        CurrentAccount bankAccount = new CurrentAccount();
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setBalance(initialBalance);
        bankAccount.setOverDraft(overDraft);
        bankAccount.setClient(client);
        bankAccount.setCreateAcc(new Date());

        return bankAccountMapper.fromCurrentBankAccount(bankAccountRepository.save(bankAccount));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long clientID) throws ClientNotFoundException {
        Client client = clientRepository.findById(clientID).orElse(null);
        if (client == null) {
            throw new ClientNotFoundException("Client not found");
        }

        SavingAccount bankAccount = new SavingAccount();
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setBalance(initialBalance);
        bankAccount.setInterestRate(interestRate);
        bankAccount.setClient(client);
        bankAccount.setCreateAcc(new Date());

        return bankAccountMapper.fromSavingBankAccount(bankAccountRepository.save(bankAccount));
    }

    @Override
    public List<ClientDTO> listClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream().map(bankAccountMapper::fromClient).collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String accountID) throws BankAccountNotFound {
        BankAccount bankAccount = bankAccountRepository.findById(accountID)
                .orElseThrow(() -> new BankAccountNotFound("Account not found"));
        if (bankAccount instanceof SavingAccount savingAccount) {
            return bankAccountMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountID, double amount, String description) throws BankAccountNotFound, InsufficientBalanceException {
        BankAccount bankAccount = bankAccountRepository.findById(accountID)
                .orElseThrow(() -> new BankAccountNotFound("Account not found"));
        if (bankAccount.getBalance() < amount){
            throw new InsufficientBalanceException("Insufficient funds to conduct transaction");
        }

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountID, double amount, String description) throws BankAccountNotFound {
        BankAccount bankAccount = bankAccountRepository.findById(accountID)
                .orElseThrow(() -> new BankAccountNotFound("Account not found"));

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String fromAccountID, String toAccountID, double amount) throws InsufficientBalanceException, BankAccountNotFound {
        debit(fromAccountID, amount, "Transfer to " + toAccountID);
        credit(fromAccountID, amount, "Transfer from " + fromAccountID);
    }

    @Override
    public List<BankAccountDTO> listBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        return bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount savingAccount) {
                return bankAccountMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankAccountMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public ClientDTO getClientById(Long clientID) throws ClientNotFoundException {
        Client client =  clientRepository.findById(clientID)
                .orElseThrow(() -> new ClientNotFoundException("Customer Not Found"));
        return bankAccountMapper.fromClient(client);
    }

    @Override
    public ClientDTO updateClient(ClientDTO clientDTO) {
        Client client = bankAccountMapper.fromClientDTO(clientDTO);
        Client savedClient = clientRepository.save(client);
        return bankAccountMapper.fromClient(savedClient);
    }

    @Override
    public void deleteClient(Long clientID) {
        clientRepository.deleteById(clientID);
    }

    @Override
    public List<AccountOperationDTO> accountOperationHistory(String accountID) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountID);
        return accountOperations.stream().map(op -> bankAccountMapper.fromAccountOperation(op))
                .collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountID, int page, int size) throws BankAccountNotFound {
        BankAccount bankAccount = bankAccountRepository.findById(accountID).orElse(null);
        if (bankAccount == null) throw new BankAccountNotFound("Account Not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(PageRequest.of(page, size), accountID);
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS =  accountOperations.getContent().stream().map(op -> bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationsDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountID(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPage(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }


}
