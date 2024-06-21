package com.example.projectebank.web;

import com.example.projectebank.dtos.*;
import com.example.projectebank.exceptions.BankAccountNotFound;
import com.example.projectebank.exceptions.InsufficientBalanceException;
import com.example.projectebank.sevices.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRESTController {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountID}")
    public BankAccountDTO getBankAccount(@PathVariable(name = "accountID") String bankAccountId) throws BankAccountNotFound {
        return bankAccountService.getBankAccount(bankAccountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> getAllBankAccounts() {
        return bankAccountService.listBankAccounts();
    }

    @GetMapping("/accounts/{id}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable("id") String accountID) {
        return bankAccountService.accountOperationHistory(accountID);
    }

    @GetMapping("/accounts/{id}/pageOperations")
    public AccountHistoryDTO getHistory(@PathVariable("id") String accountID,
                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFound {
        return bankAccountService.getAccountHistory(accountID, page, size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws InsufficientBalanceException, BankAccountNotFound {
        bankAccountService.debit(debitDTO.getAccountID(), debitDTO.getAmount(), debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    public DebitDTO credit(@RequestBody DebitDTO creditDTO) throws BankAccountNotFound, InsufficientBalanceException {
        bankAccountService.credit(creditDTO.getAccountID(), creditDTO.getAmount(), creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferDTO) throws BankAccountNotFound, InsufficientBalanceException {
        bankAccountService.transfer(transferDTO.getAccountSource(), transferDTO.getAccountDestination(), transferDTO.getAmount());
    }
}
