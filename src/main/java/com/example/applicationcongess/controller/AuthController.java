package com.example.applicationcongess.controller;

import com.example.applicationcongess.JWT.JwtUtils;
import com.example.applicationcongess.PlayLoad.Response.Accesstokenresponse;

import com.example.applicationcongess.PlayLoad.request.*;
import com.example.applicationcongess.PlayLoad.Response.JwtResponse;
import com.example.applicationcongess.PlayLoad.Response.MessageResponse;
import com.example.applicationcongess.enums.ERole;
import com.example.applicationcongess.models.Personnel;

import com.example.applicationcongess.models.Role;
import com.example.applicationcongess.repositories.PersonnelRepository;
import com.example.applicationcongess.repositories.RoleRepo;
import com.example.applicationcongess.services.*;
import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")

public class AuthController {
    @Autowired
    private TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    final AuthenticationManager authenticationManager;
    @Autowired
    PersonnelRepository userRepository;
    @Autowired
    RoleRepo roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    Personnelserv userServices;
   // @Autowired
    //RefreshTokenRepository refreshTokenRepository;
  //  @Autowired
    //RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    UserService userService;
    @GetMapping("/username/{user}")
    public Personnel getUsername(@PathVariable("user") String user){

         return userService.getbyUsername(user);

    }
    @GetMapping("/userid/{userid}")
    public Personnel getbyid(@PathVariable("userid") long  userid){

        return userService.retrieveUser(userid);
    }
    @PutMapping("/modifieruser/{iduser}")
    public Personnel modifieruser(@PathVariable("iduser") long  iduser  ,@RequestBody Updateuser updatepersonnel  ) throws MessagingException {

        return userService.updateUser(iduser,updatepersonnel);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println(loginRequest.getUsername());
            System.out.println(loginRequest.getPassword());
            System.out.println("hhh");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            System.out.println("hhhh");
            System.out.println(loginRequest.getUsername());
            System.out.println(loginRequest.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(authentication);
            Long userId = userDetails.getCin();
            Personnel personnel = userRepository.findById(userId).orElse(null);
            personnel.setJwt(jwt);
            userRepository.save(personnel);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException e) {
            System.out.println("Erreur d'authentification : " + e.getMessage());
        }
        return null;
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {

        if (signUpRequest.getEmail() == null) {

            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        // Create new user's account
        Personnel user = new Personnel(/*signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())*/);

        user.setEmail(signUpRequest.getEmail());
user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));


        List<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>(); {
        };
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.Role_collaborateur)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "Role_collaborateur":
                        Role adminRole = roleRepository.findByName(ERole.Role_collaborateur)
                                .orElseThrow(() -> new RuntimeException("Error: Role_collaborateur is not found."));
                        roles.add(adminRole);
                        break;
                    case "Role_gestionnaire":
                        Role modRole = roleRepository.findByName(ERole.Role_gestionnaire)
                                .orElseThrow(() -> new RuntimeException("Error: Role_gestionnaire is not found."));
                        roles.add(modRole);
                        break;
                    case "Role_manager":
                        Role livRole = roleRepository.findByName(ERole.Role_manager)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(livRole);
                        break;
                    case "Role_manager2":
                        Role clientRole = roleRepository.findByName(ERole.Role_manager2)
                                .orElseThrow(() -> new RuntimeException("Error: Role_manager2 is not found."));
                        roles.add(clientRole);

                    default:
                        Role userRole = roleRepository.findByName(ERole.Role_collaborateur)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }







    @Autowired
    PersonnelRepository personnelRepository;

  /*  @PostMapping("/refreshToken")
    public ResponseEntity<Accesstokenresponse> refreshToken(@RequestBody TokenRefreshRequest refreshTokenRequest) {

        System.out.println("avant");
        Personnel userInfo = jwtUtils.getUserFromAccessToken(refreshTokenRequest.getRefreshToken());
        System.out.println(userInfo);
        System.out.println(userInfo.getUsername());
        System.out.println(userInfo.getPassword());

        return refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getPersonnel)
                .map(user -> {
                    String accessToken = jwtUtils.generateAccessToken(userInfo.getCin());



                    userInfo.setJwt(accessToken);
                    personnelRepository.save(userInfo);
                    return ResponseEntity.ok(new Accesstokenresponse(accessToken,
                            refreshTokenRequest.getRefreshToken()
                    ));

                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
    }
*/
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    ActivitiConfig activitiConfig;
@PostMapping("/envoi/{recipient}/{subject}/{content}")
    public void sendEmail( @PathVariable ("recipient") String recipient, @PathVariable("subject") String subject, @PathVariable("content") String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }




    @GetMapping("deploy")
    public void process(){
        activitiConfig.deployMyProcess();
    }

    @PostMapping("attributemanager/{personnelId}/{managerId}/{managerLevel}")
    public Personnel attribuerManager(@PathVariable("personnelId") Long personnelId,@PathVariable("managerId") Long managerId, @PathVariable("managerLevel") int managerLevel) {
        return userServices.attribuerManager(personnelId,managerId,managerLevel);
    }
@PostMapping("sendcode/{email}")
    public String sendcode(@PathVariable("email") String email ) throws MessagingException {
    return userService.SendCode(email);
}
@PostMapping("resetpassword/{code}/{newPassword}")
    public String ResetPassword(@PathVariable("code") String code,@PathVariable("newPassword") String newPassword){
    return userService.ResetPassword(code,newPassword);
    }
}





//@PostMapping("/generatecodeverif")
  //  public Object  verifcode(){
    //   return  userServices.generateodeverif();}
//}



