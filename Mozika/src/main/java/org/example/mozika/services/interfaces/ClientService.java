package org.example.mozika.services.interfaces;

import org.example.mozika.models.Client;
import org.example.mozika.models.dto.ClientSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ClientService {
    Page<Client> getAllClient(Pageable pageable);

    Page<Client> getAllClient(Pageable pageable, ClientSearch object);

    Client getClientById(Long id);

    public String exportClientToCSV(List<Client> client);

    

    Client createClient(Client client);

    Client updateClient(Long id, Client client);

    void deleteClient(Long id);
    

}
