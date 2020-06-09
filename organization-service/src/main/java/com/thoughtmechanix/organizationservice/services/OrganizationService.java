package com.thoughtmechanix.organizationservice.services;

import com.thoughtmechanix.organizationservice.model.Organization;
import com.thoughtmechanix.organizationservice.repository.OrganizationRepository;
import com.thoughtmechanix.organizationservice.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    public Organization getOrg(String organizationId) {
        if(orgRepository.findById(organizationId).isPresent())
            return orgRepository.findById(organizationId).get();
        return new Organization();
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);

    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
    }

    public void deleteOrg(Organization org){
        orgRepository.delete(org);
    }
}
