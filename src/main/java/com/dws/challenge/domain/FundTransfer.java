package com.dws.challenge.domain;



import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data

public class FundTransfer
{    
    @NotNull
    @NotEmpty
    private String fromAccount;
    
    @NotNull
    @NotEmpty
    private String toAccount;
    
    private BigDecimal amount;
	
}