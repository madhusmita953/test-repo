package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransfer;
import com.dws.challenge.exception.AccountException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InvalidAmountException;


import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    
   

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

	@Override
	public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount)throws InvalidAmountException,AccountException {
		// TODO Auto-generated method stub
		
		if (amount.intValue() <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
		Account sourceAccount = getAccount(sourceAccountNumber);
		BigDecimal sourceBalance = sourceAccount.getBalance();
		
		if (sourceBalance.intValue() < amount.intValue()) {
            throw new InvalidAmountException("Insufficient Balance");
        }
		
		Account targetAccount = getAccount(targetAccountNumber);
		
		if (targetAccount == null) {
            throw new AccountException("Destination Account does not exist");
        }
		BigDecimal newSourceBalance = sourceBalance.subtract(sourceBalance);
        sourceAccount.setBalance(newSourceBalance);
        accounts.put(sourceAccount.getAccountId(),sourceAccount);

        BigDecimal targetBalance = targetAccount.getBalance();
        BigDecimal newTargetBalance = targetBalance.add(targetBalance);
        targetAccount.setBalance(newTargetBalance);
        accounts.put(sourceAccount.getAccountId(),targetAccount);
        
       
		
	}
	
	

}
