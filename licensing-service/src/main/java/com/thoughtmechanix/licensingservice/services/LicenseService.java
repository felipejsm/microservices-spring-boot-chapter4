package com.thoughtmechanix.licensingservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.thoughtmechanix.licensingservice.clients.OrganizationDiscoveryClient;
import com.thoughtmechanix.licensingservice.clients.OrganizationFeignClient;
import com.thoughtmechanix.licensingservice.clients.OrganizationRestTemplateClient;
import com.thoughtmechanix.licensingservice.config.ServiceConfig;
import com.thoughtmechanix.licensingservice.model.License;
import com.thoughtmechanix.licensingservice.model.Organization;
import com.thoughtmechanix.licensingservice.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LicenseService {
    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ServiceConfig serviceConfig;


    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;
    private Organization retrieveOrgInfo(String organizationId, String clientType) {
            Organization organization = null;
            switch (clientType) {
                case "feign" :
                    System.out.println("I am using the feign client");
                    organization = organizationFeignClient.getOrganization(organizationId);
                    break;
                case "rest" :
                    System.out.println("I am using the rest client");
                    organization = organizationRestClient.getOrganization(organizationId);
                    break;
                case "discovery" :
                    System.out.println("I am using the discovery client");
                    organization = organizationDiscoveryClient.getOrganization(organizationId);
                    break;
                default:
                    organization = organizationRestClient.getOrganization(organizationId);
            }
        return organization;
    }
    public License getLicense(String organizationId, String licenseId, String clientType) {
        License license = this.licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        Organization organization = retrieveOrgInfo(organizationId, clientType);
        return license
                .withOrganizationName( organization.getName())
                .withContactName( organization.getContactName())
                .withContactEmail( organization.getContactEmail() )
                .withContactPhone( organization.getContactPhone() )
                .withComment(this.serviceConfig.getExampleProperty());
    }
    @HystrixCommand
    public List<License> getLicensesByOrg(String organizationId) {
        return this.licenseRepository.findByOrganizationId(organizationId);
    }

    public void saveLicense(License license) {
        license.withId(UUID.randomUUID().toString());
        licenseRepository.save(license);
    }

    public void updateLicense(License license){
        licenseRepository.save(license);
    }

    public void deleteLicense(License license){
        licenseRepository.delete(license);
    }
}
