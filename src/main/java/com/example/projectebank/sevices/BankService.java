package com.example.projectebank.sevices;

import com.example.projectebank.entities.BankAccount;
import com.example.projectebank.entities.CurrentAccount;
import com.example.projectebank.entities.SavingAccount;
import com.example.projectebank.repositories.BankAccountRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class BankService {
    private BankAccountRepository bankAccountRepository;

    public void consult() {
        BankAccount bankAccount = bankAccountRepository.findById("1bffc836-8bd0-47b5-9dbe-0851c1e8007a").orElse(null);
        if (bankAccount != null) {
            System.out.println("****************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreateAcc());
            System.out.println(bankAccount.getClient().getName());
            System.out.println(bankAccount.getClass().getSimpleName());

            if (bankAccount instanceof CurrentAccount){
                System.out.println("This is a current Account");
                System.out.println("Overdraft is: " + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount){
                System.out.println("This is a savings account");
                System.out.println("Interest Rate is: " + ((SavingAccount) bankAccount).getInterestRate());
            }

            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("****************");
                System.out.println(op.getType());
                System.out.println(op.getAmount());
                System.out.println(op.getOperationDate());
            });
        }
    }
}
