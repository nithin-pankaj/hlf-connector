package hlf.java.rest.client.controller;

import hlf.java.rest.client.model.ChannelOperationRequest;
import hlf.java.rest.client.model.ClientResponseModel;
import hlf.java.rest.client.service.ChannelService;
import hlf.java.rest.client.util.ESAPIUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/channel")
public class ChannelOperationController {

  @Autowired private ChannelService channelService;

  @PostMapping("/create")
  public ResponseEntity<ClientResponseModel> createChannel(
      @RequestBody ChannelOperationRequest channelCreationRequest) {

    channelCreationRequest = (ChannelOperationRequest) ESAPIUtil.stripXSSForObject(channelCreationRequest);

    ClientResponseModel response = channelService.createChannel(channelCreationRequest);
    return ResponseEntity.status(response.getCode()).body(response);
  }

  @PostMapping("/join")
  public ResponseEntity<ClientResponseModel> joinChannel(
      @RequestBody ChannelOperationRequest channelJoinRequest) {

    channelJoinRequest = (ChannelOperationRequest) ESAPIUtil.stripXSSForObject(channelJoinRequest);

    ClientResponseModel response = channelService.joinChannel(channelJoinRequest);
    return ResponseEntity.status(response.getCode()).body(response);
  }

  @GetMapping("/members-mspid")
  public ResponseEntity<Set<String>> getChannelMembersMSPID(
      @RequestParam("channel_name") String channelName) {

    channelName = (String) ESAPIUtil.stripXSSForObject(channelName);

    return new ResponseEntity<>(channelService.getChannelMembersMSPID(channelName), HttpStatus.OK);
  }
}
