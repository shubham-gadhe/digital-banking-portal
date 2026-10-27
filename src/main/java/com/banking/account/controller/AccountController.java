package com.banking.account.controller;

import com.banking.account.dto.AccountRequestDTO;
import com.banking.account.dto.AccountResponseDTO;
import com.banking.account.entity.AccountStatus;
import com.banking.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import com.banking.auth.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController
{
    private final AccountService accountService;
    private final JwtService jwtService;

    //////////////////////////////////////////////////////////////////
    //
    //  Constructor Name    : AccountController
    //
    //  Description         : Initializes the AccountService and
    //                        JwtService dependencies using
    //                        constructor injection.
    //
    //  Author              : Shubham Somanath Gadhe
    //
    //  Date                : 13/09/2026
    //
    //////////////////////////////////////////////////////////////////
    
    public AccountController(AccountService accountService, JwtService jwtService)
    {
        this.accountService = accountService;
        this.jwtService = jwtService;
    }
    
    //////////////////////////////////////////////////////////////////
    //
    //  Method Name         : createAccount
    //
    //  Description         : Extracts the customer's email from the
    //                        JWT token and retrieves the logged-in
    //                        customer's ID. Verifies that the requested
    //                        customer ID matches the authenticated
    //                        customer before creating a bank account.
    //                        Returns the newly created account details.
    //
    //  Author              : Shubham Somanath Gadhe
    //
    //  Date                : 13/09/2026
    //
    //////////////////////////////////////////////////////////////////

    @Operation(
            summary = "Create bank account",
            description = "Creates a new bank account for the authenticated customer"
    )
    @PostMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.CREATED)    
    public AccountResponseDTO createAccount(@PathVariable("customerId") Long customerID,
                                            @Valid @RequestBody AccountRequestDTO request,
                                            @RequestHeader("Authorization") String authorizationHeader)
    {
        String token = authorizationHeader.substring(7);

        String email = jwtService.extractEmail(token);

        Long loggedInCustomerId = accountService.getCustomerIdByEmail(email);

        if(!customerID.equals(loggedInCustomerId))
        {
            throw new IllegalArgumentException("You are not authorized to create an account for this customer");
        }

        return accountService.createAccount(customerID, request);
    }
    
    //////////////////////////////////////////////////////////////////
    //
    //  Method Name         : updateAccountStatus
    //
    //  Description         : Updates the status of an existing bank
    //                        account using the provided account ID
    //                        and new account status. Returns the
    //                        updated account details.
    //
    //  Author              : Shubham Somanath Gadhe
    //
    //  Date                : 13/09/2026
    //
    //////////////////////////////////////////////////////////////////

    @Operation(
            summary = "Update account status",
            description = "Updates the status of an existing bank account"
    )
    @PutMapping("/{id}/status")
    
    public AccountResponseDTO updateAccountStatus(@PathVariable Long id, @RequestParam AccountStatus status)
    {
        return accountService.updateAccountStatus(id, status);
    }

    //////////////////////////////////////////////////////////////////
    //
    //  Method Name         : getAccountBalance
    //
    //  Description         : Extracts the customer's email from the
    //                        JWT token and retrieves the authenticated
    //                        customer's ID. Fetches the account balance
    //                        and account details after verifying the
    //                        account belongs to that customer.
    //
    //  Author              : Shubham Somanath Gadhe
    //
    //  Date                : 13/09/2026
    //
    //////////////////////////////////////////////////////////////////
    
    @Operation(
            summary = "Get account balance",
            description = "Returns the balance and details of an account belonging to the authenticated customer"
    )
    @GetMapping("/number/{accountNumber}/balance")
   
    public AccountResponseDTO getAccountBalance(@PathVariable String accountNumber,
                                                @RequestHeader("Authorization") String authorizationHeader)
    {
        String token = authorizationHeader.substring(7);

        String email = jwtService.extractEmail(token);

        Long customerId = accountService.getCustomerIdByEmail(email);

        return accountService.getAccountBalance(accountNumber, customerId);
    }

    //////////////////////////////////////////////////////////////////
    //
    //  Method Name         : getMyAccounts
    //
    //  Description         : Extracts the customer's email from the
    //                        JWT token and retrieves the authenticated
    //                        customer's ID. Fetches and returns a list
    //                        of all bank accounts belonging to the
    //                        logged-in customer.
    //
    //  Author              : Shubham Somanath Gadhe
    //
    //  Date                : 13/09/2026
    //
    //////////////////////////////////////////////////////////////////

    @Operation(
            summary = "Get my accounts",
            description = "Returns all the bank accounts belonging to the currently authenticated customer"
    )
    @GetMapping("/my-accounts")
    
    public List<AccountResponseDTO> getMyAccounts(@RequestHeader("Authorization") String authorizationHeader)
    {
        String token = authorizationHeader.substring(7);

        String email = jwtService.extractEmail(token);

        Long customerId = accountService.getCustomerIdByEmail(email);

        return accountService.getCustomerAccounts(customerId);
    }

    //////////////////////////////////////////////////////////////////
    //
    //  Method Name         : getAccountById
    //
    //  Description         : Extracts the customer's email from the
    //                        JWT token and retrieves the authenticated
    //                        customer's ID. Retrieves the specified
    //                        account after verifying that it belongs
    //                        to the logged-in customer and returns
    //                        the account details.
    //
    //  Author              : Shubham Somanath Gadhe
    //
    //  Date                : 13/09/2026
    //
    //////////////////////////////////////////////////////////////////

    @Operation(
            summary = "Get account by ID",
            description = "Return details of a specific bank account belonging to the authenticated customer"
    )
    @GetMapping("/{accountId}")
    
    public AccountResponseDTO getAccountById(@PathVariable Long accountId,
                                             @RequestHeader("Authorization") String authorizationHeader)
    {
        String token = authorizationHeader.substring(7);

        String email = jwtService.extractEmail(token);

        Long customerId = accountService.getCustomerIdByEmail(email);

        return accountService.getCustomerAccount(customerId, accountId);
    }
}
