package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")//only for users who log in
public class ProfileController {
    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;

    }

    @GetMapping
    public ResponseEntity<Profile> getProfile(Principal principal) {
        String username = principal.getName();
      User user = userDao.getByUserName(username);
       int userID = user.getId();

        Profile profile = profileDao.getByUserId(userID);
        if (profile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    //   профіль поточного користувача
    @PutMapping
    public ResponseEntity<Void> updateProfile(@RequestBody Profile profile,Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        // Захист   користувач може оновлювати лише свій профіль
        profile.setUserId(user.getId());
        profileDao.update(profile);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
