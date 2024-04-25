package com.yelm.memberpj.user;


import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/user")
public class UserController {
    //TODO: Controller Implementation

    @GetMapping("/hello")
    public String test(){
        return "hello";
    }

    @PostMapping("/join")
    public ResponseEntity signup(@RequestBody UserDto.UserJoinDto userJoinDto){
        // Todo: UserDto, Service Layer Implementation
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity getUserList(@Positive @RequestParam int page,
                                    @Positive @RequestParam int pageSize,
                                    @RequestParam(required = false, defaultValue = "") String sort){
        // Todo: Service Layer Implementation

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity updateUser(@PathVariable("user-id") @Positive String userId){
        // Todo: Service Layer Implementation

        // Todo: updated user info 함께 전달
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
