package com.lambdaschool.tempEC.controller;

import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import io.swagger.annotations.*;
import com.lambdaschool.tempEC.models.Conversation;
import com.lambdaschool.tempEC.models.ErrorDetail;
import com.lambdaschool.tempEC.services.ConversationService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.RandomSaltGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("")
public class EmpConvoController {

    @Autowired
    private ConversationService convoService;

    @ApiOperation(value = "return all conversations", response = Conversation.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integr", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")})
    @GetMapping(value="/conversations")
    public ResponseEntity<?> findAllConvos() {
        return new ResponseEntity<>(convoService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value="/mobilecheck")
    public ResponseEntity<?> mobileTest() {
        return new ResponseEntity<>("Jacob Ryans, Josh Halvorson, Omar Okla, Rudy Mejia, Marlene Salangsang, Chariton Shumway, Nolan Allen", HttpStatus.OK);
    }

    @ApiOperation(value = "Create new conversation.", response=Conversation.class)
    @ApiResponses(value = {
            @ApiResponse(code=201, message = "Author book added successfully", response = Conversation.class),
            @ApiResponse(code=404, message = "Incorrect endpoint.", response = EntityNotFoundException.class),
            @ApiResponse(code=500, message = "Invalid request body.", response = ErrorDetail.class)
    })
    @PostMapping(value="/conversations")
    public ResponseEntity<?> createNewConversation(@Valid @RequestBody Conversation newConvo) {
        Conversation createdConvo = convoService.save(newConvo);
        String ACCOUNT_SID = System.getenv("TWILIO_SID");
        String AUTH_TOKEN = System.getenv("TWILIO_TOKEN");
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
        StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setPassword(System.getenv("SECRET"));
        textEncryptor.setAlgorithm(System.getenv("METHOD"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String id = textEncryptor.encrypt(Long.toString(createdConvo.getConversationid()));
        StringBuilder editedId = new StringBuilder(id);
        String number = "+18476968785";
        switch(newConvo.getSchool()) {
            case "michigan" :
                number = "+18476968785";
                break;
            case "northwestern" :
                number = "+18476968785";
                break;
        }
        if(id.indexOf('/') >= 0) {
            editedId.setCharAt(id.indexOf('/'), '!');
        } else {
            System.out.println("Not a slash");
        }
        params.add(new BasicNameValuePair("To", newConvo.getFfnumber()));
        params.add(new BasicNameValuePair("From", number));
        params.add(new BasicNameValuePair("Body", "Hi " + newConvo.getFfname() + ", a friend or loved one is reaching out for support. Click here to help them: " + "https://empoweredconvo.com/" + createdConvo.getSchool() + "/learn/" + "?cid=" + editedId));
        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        try { messageFactory.create(params); createdConvo.setFfnumber(null); } catch(Exception exc) { System.out.println(exc); };
        return new ResponseEntity<>(createdConvo, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Deletes conversation.", response=String.class)
    @ApiResponses(value = {
            @ApiResponse(code=200, message = "Conversation deleted successfully", response = String.class),
            @ApiResponse(code=404, message = "Conversation ID not valid.", response = EntityNotFoundException.class),
    })
    @DeleteMapping(value="/conversations/finished/{conversationid}")
    public ResponseEntity<?> finishConversation(@PathVariable String conversationid) {
        ArrayList<Conversation> list = new ArrayList<>();
        StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setPassword(System.getenv("SECRET"));
        textEncryptor.setAlgorithm(System.getenv("METHOD"));
        convoService.findAll().iterator().forEachRemaining(list::add);
        StringBuilder editedId = new StringBuilder(conversationid);
        if(conversationid.indexOf('!') >= 0) {
            editedId.setCharAt(conversationid.indexOf('!'), '/');
        } else {
            System.out.println("Not a slash");
        }
        conversationid = textEncryptor.decrypt(editedId.toString());
        Long newId = Long.parseLong(conversationid);
        for(Conversation c : list) {
            if(c.getConversationid() == newId) {
                break;
            } else if(c.getConversationid() >= newId) {
                return new ResponseEntity<>("Conversation already deleted", HttpStatus.OK);
            } else if(list.get(list.size()-1).getConversationid() < newId) {
                return new ResponseEntity<>("Conversation already deleted or doesn't exist", HttpStatus.OK);
            }
        }
        Conversation foundConvo = convoService.findById(newId);
        String ACCOUNT_SID = System.getenv("TWILIO_SID");
        String AUTH_TOKEN = System.getenv("TWILIO_TOKEN");
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("To", textEncryptor.decrypt(foundConvo.getSurvivornumber())));
        params.add(new BasicNameValuePair("From", "+18476968785"));
        params.add(new BasicNameValuePair("Body", textEncryptor.decrypt(foundConvo.getFfname()) + " is ready to speak with you and has read resources to prepare themselves. Thank you for trusting in the Empowered Conversations service."));
        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        try { messageFactory.create(params); convoService.delete(newId); } catch(Exception exc) { throw new EntityNotFoundException(exc.toString()); };
        return new ResponseEntity<>("Conversation " + conversationid + " has been closed.", HttpStatus.OK);
    }
}
