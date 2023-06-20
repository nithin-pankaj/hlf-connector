package hlf.java.rest.client.controller;

import hlf.java.rest.client.model.ChaincodeOperations;
import hlf.java.rest.client.model.ChaincodeOperationsType;
import hlf.java.rest.client.service.ChaincodeOperationsService;
import hlf.java.rest.client.util.ESAPIUtil;
import lombok.extern.slf4j.Slf4j;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/chaincode")
public class ChaincodeOperationsController {

  @Autowired private ChaincodeOperationsService chaincodeOperationsService;

  @PutMapping(value = "/operations")
  public ResponseEntity<String> performChaincodeOperation(
      @RequestParam("network_name") @Validated String networkName,
      @RequestParam("operations_type") @Validated ChaincodeOperationsType operationsType,
      @RequestPart("chaincodeOperations") ChaincodeOperations chaincodeOperations,
      // accept optional collection configuration for the approval and commit
      @RequestPart(value = "collection_config", required = false)
          MultipartFile collectionConfigFile) {

    networkName = (String) ESAPIUtil.stripXSSForObject(chaincodeOperations);
    chaincodeOperations = (ChaincodeOperations) ESAPIUtil.stripXSSForObject(chaincodeOperations);
    collectionConfigFile = (MultipartFile) ESAPIUtil.stripXSSForObject(collectionConfigFile);

    return new ResponseEntity<>(
        chaincodeOperationsService.performChaincodeOperation(
            networkName,
            chaincodeOperations,
            operationsType,
            Optional.ofNullable(collectionConfigFile)),
        HttpStatus.OK);
  }

  @GetMapping(value = "/sequence")
  public ResponseEntity<String> getCurrentSequence(
      @RequestParam("network_name") @Validated String networkName,
      @RequestParam("chaincode_name") @Validated String chaincodeName,
      @RequestParam("chaincode_version") @Validated String chaincodeVersion) {

    networkName = (String) ESAPIUtil.stripXSSForObject(networkName);
    chaincodeName = (String) ESAPIUtil.stripXSSForObject(chaincodeName);
    chaincodeVersion = (String) ESAPIUtil.stripXSSForObject(chaincodeVersion);

    return new ResponseEntity<>(
        chaincodeOperationsService.getCurrentSequence(networkName, chaincodeName, chaincodeVersion),
        HttpStatus.OK);
  }

  @GetMapping(value = "/packageId")
  public ResponseEntity<String> getCurrentPackageId(
      @RequestParam("network_name") @Validated String networkName,
      @RequestParam("chaincode_name") @Validated String chaincodeName,
      @RequestParam("chaincode_version") @Validated String chaincodeVersion) {

    networkName = (String) ESAPIUtil.stripXSSForObject(networkName);
    chaincodeName = (String) ESAPIUtil.stripXSSForObject(chaincodeName);
    chaincodeVersion = (String) ESAPIUtil.stripXSSForObject(chaincodeVersion);

    return new ResponseEntity<>(
        chaincodeOperationsService.getCurrentPackageId(
            networkName, chaincodeName, chaincodeVersion),
        HttpStatus.OK);
  }

  @GetMapping(value = "/approved-organisations")
  public ResponseEntity<Set<String>> getApprovedOrganisationListForSmartContract(
      @RequestParam("network_name") @Validated String networkName,
      @RequestParam("chaincode_name") String chaincodeName,
      @RequestParam("chaincode_version") String chaincodeVersion,
      @RequestParam("sequence") Long sequence,
      @RequestParam(value = "init_required", defaultValue = "false") boolean initRequired) {

    networkName = ESAPI.encoder().encodeForHTML(networkName);
    chaincodeName = (String) ESAPIUtil.stripXSSForObject(chaincodeName);
    chaincodeVersion = (String) ESAPIUtil.stripXSSForObject(chaincodeVersion);

    ChaincodeOperations chaincodeOperations =
        ChaincodeOperations.builder()
            .chaincodeName(chaincodeName)
            .chaincodeVersion(chaincodeVersion)
            .sequence(sequence)
            .initRequired(initRequired)
            .build();
    return new ResponseEntity<>(
        chaincodeOperationsService.getApprovedOrganizations(
            networkName, chaincodeOperations, Optional.empty(), Optional.empty()),
        HttpStatus.OK);
  }
}
