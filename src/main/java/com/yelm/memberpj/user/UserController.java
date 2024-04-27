package com.yelm.memberpj.user;


import com.yelm.memberpj.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    //TODO: Controller Implementation
    private final UserService userService;

    @GetMapping("/hello")
    public String test(){
        return "hello";
    }

    @PostMapping("/join")
    public ResponseEntity signup(@RequestBody @Valid UserDto.UserJoinDto userJoinDto){

        userService.postUser(userJoinDto);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity getUserList(@Positive @RequestParam int page,
                                    @Positive @RequestParam int pageSize,
                                    @RequestParam(required = false, defaultValue = "") String sort){
        // Todo: Service Layer Implementation

        return userService.getUsers(page, pageSize, sort);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity updateUser(@PathVariable("user-id") String username,
                                     @RequestBody @Valid UserDto.PatchDto patchDto){
        // Todo: Service Layer Implementation
        userService.patchUser(username, patchDto);

        // Todo: updated user info 함께 전달
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
