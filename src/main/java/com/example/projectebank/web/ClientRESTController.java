package com.example.projectebank.web;

import com.example.projectebank.dtos.ClientDTO;
import com.example.projectebank.exceptions.ClientNotFoundException;
import com.example.projectebank.sevices.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200/")
public class ClientRESTController {
    private BankAccountService bankAccountService;
    @GetMapping("/clients")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<ClientDTO> clients(){
        return bankAccountService.listClients();
    }

    @GetMapping("/clients/search")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<ClientDTO> searchClients(@RequestParam(name = "keyword", defaultValue = "") String keyword){
        return bankAccountService.searchClients(keyword);
    }

    @GetMapping("/clients/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ClientDTO getClientById(@PathVariable(name = "id") Long clientId) throws ClientNotFoundException {
        return bankAccountService.getClientById(clientId);
    }

    @PostMapping("/clients")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ClientDTO saveClient(@RequestBody ClientDTO clientDTO) {
        return bankAccountService.saveClient(clientDTO);
    }

    @PutMapping("/clients/{clientID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ClientDTO updateClient(@PathVariable(name = "clientID") Long Id ,@RequestBody ClientDTO clientDTO) {
        clientDTO.setId(Id);
        return bankAccountService.updateClient(clientDTO);
    }

    @DeleteMapping("/clients/{clientID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteClient(@PathVariable(name = "clientID") Long clientId) {
        bankAccountService.deleteClient(clientId);
    }
}