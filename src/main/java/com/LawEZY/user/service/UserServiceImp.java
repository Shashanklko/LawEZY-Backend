package com.LawEZY.user.service;

import com.LawEZY.user.dto.UserRequest;
import com.LawEZY.user.dto.UserResponse;
import com.LawEZY.user.entity.User;
import com.LawEZY.user.exception.ResourceNotFoundException;
import com.LawEZY.user.repository.UserRepository;
import com.LawEZY.user.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// @Service tells Spring: "This is a special class that holds business logic." 
// Spring will automatically create an object of this class when the app starts.
@Service
public class UserServiceImp implements UserService {

    // @Autowired tells Spring: "Look for a UserRepository bean and plug it in here automatically."
    // This connects our Service layer to the Database layer without needing 'new UserRepository()'.
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- CREATE A NEW USER ---
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        
        // 1. The Controller gave us a UserRequest DTO (data from the frontend).
        // The Database only accepts 'User' entities. So, we call our helper method 
        // down below to translate the Request DTO into a Database Entity.
        User user = mapToEntity(userRequest);
        
        // 2. We ask the UserRepository to save this new Entity into the MySQL/MongoDB database.
        // It returns the saved User (which now has a generated ID attached to it!).
        User savedUser = userRepository.save(user);
        
        // 3. We can't send the Entity back to the Controller (security/best-practices).
        // So, we translate the saved Entity into a UserResponse DTO and return it.
        return mapToResponse(savedUser); 
    }

    // --- GET ONE USER BY ID ---
    @Override
    public UserResponse getUserById(Long id) {
        // 1. We ask the DB to find the user by ID. 
        // findById returns an 'Optional' (because the user might not exist).
        // .orElseThrow() means: "If you find it, give me the User. If not, crash gracefully with this error."
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // 2. Translate the found database Entity into a secure Response DTO.
        return mapToResponse(user);
    }

    // --- GET ALL USERS ---
    @Override
    public List<UserResponse> getAllUsers() {
        // 1. Get a list of ALL User entities directly from the database table.
        List<User> users = userRepository.findAll();
        
        // 2. We have a List of Entities, but we need to return a List of DTOs.
        // .stream() opens the list so we can loop through it.
        // .map(this::mapToResponse) translates EVERY single Entity in the list into a DTO.
        // .collect(Collectors.toList()) packs them all back up into a brand new List.
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- UPDATE AN EXISTING USER ---
    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        
        // 1. Check if the user we are trying to update actually exists in the DB.
        // If not, throw an error.
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Overwrite the old database data with the new data from the UserRequest DTO.
        existingUser.setFirstname(userRequest.getFirstname());
        existingUser.setLastname(userRequest.getLastname());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        existingUser.setRole(userRequest.getRole());

        // 3. Save the modified user back to the database. (Spring knows to UPDATE instead of CREATE because it already has an ID).
        User updatedUser = userRepository.save(existingUser);
        
        // 4. Translate the updated database Entity into a secure Response DTO.
        return mapToResponse(updatedUser);
    }

    // --- DELETE A USER ---
    @Override
    public void deleteUser(Long id) {
        // Tells the database to permanently delete the row matching this ID.
        userRepository.deleteById(id);
    }

    // ==========================================
    // --- HELPER TRANSLATION (MAPPING) METHODS ---
    // ==========================================

    // This method takes a UserRequest (Frontend Data) and turns it into a User (Database Data)
    private User mapToEntity(UserRequest request) {
        User user = new User(); // Create a blank Database User
        user.setFirstname(request.getFirstname()); // Copy the First Name over
        user.setLastname(request.getLastname());   // Copy the Last Name over
        user.setEmail(request.getEmail());         // Copy the Email over
        user.setPassword(passwordEncoder.encode(request.getPassword()));   // Copy the Password over
        user.setRole(request.getRole());           // Copy the Role over
        return user; // Return the fully packed Database User
    }

    // This method takes a User (Database Data) and turns it into a UserResponse (Frontend Data)
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse(); // Create a blank Response
        response.setId(user.getId());               // Expose the Database ID
        response.setFirstname(user.getFirstname()); // Expose the First Name
        response.setLastname(user.getLastname());   // Expose the Last Name
        response.setEmail(user.getEmail());         // Expose the Email
        response.setRole(user.getRole());           // Expose the Role
        
        // NOTICE: We specifically DO NOT add the password to 'response'.
        // This guarantees the password never accidentally gets sent back to the internet!
        
        return response; // Return the safe, secure Response object
    }
}
