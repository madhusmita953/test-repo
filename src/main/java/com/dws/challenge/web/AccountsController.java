package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransfer;
import com.dws.challenge.exception.DuplicateAccountIdException;

import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
  
  @PostMapping(value="/fund-transfer",consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> fundTransfer(@RequestBody FundTransfer fundTransfer) {
	  log.info("Fund Transfer {}");
	  try {
		  this.accountsService.fundTransfer(
				  fundTransfer.getFromAccount(),
				  fundTransfer.getToAccount(),
				  fundTransfer.getAmount());

	} catch (Exception e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
	  NotificationService emailNotification=(Account sourceAccount,String message)->
	  System.out.println(fundTransfer.getAmount()+"Amount Debited from the Account"+ message);
	  
	  Account sourceAccount=new Account(fundTransfer.getFromAccount(),fundTransfer.getAmount());
	  emailNotification.notifyAboutTransfer(sourceAccount, ""+fundTransfer.getFromAccount());
	  
	  
	  NotificationService emailNotification1=(Account targetAccount,String message)->
	  System.out.println(fundTransfer.getAmount()+"Amount Credited to the Account"+ message);
	  
	  Account targetAccount=new Account(fundTransfer.getToAccount(),fundTransfer.getAmount());
	  emailNotification1.notifyAboutTransfer(targetAccount, ""+fundTransfer.getToAccount());
	  
	  return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
