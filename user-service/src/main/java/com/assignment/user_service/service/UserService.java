package com.assignment.user_service.service;

import com.assignment.user_service.dto.UpdateUserRequest;
import com.assignment.user_service.dto.UserResponse;
import com.assignment.user_service.entity.User;
import com.assignment.user_service.event.UserEvent;
import com.assignment.user_service.event.UserUpdatedEvent;
import com.assignment.user_service.exception.EmailAlreadyExistsException;
import com.assignment.user_service.exception.UserAccessDeniedException;
import com.assignment.user_service.exception.UserNotFoundException;
import com.assignment.user_service.mapper.UserMapper;
import com.assignment.user_service.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }

    public UserResponse getById(UUID requestedId, UUID authenticatedUserId) {
        assertSelf(requestedId, authenticatedUserId);
        User user = userRepository.findById(requestedId)
                .orElseThrow(() -> new UserNotFoundException(requestedId));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(UUID requestedId, UUID authenticatedUserId, UpdateUserRequest request) {
        assertSelf(requestedId, authenticatedUserId);

        User user = userRepository.findById(requestedId)
                .orElseThrow(() -> new UserNotFoundException(requestedId));

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        User saved = userRepository.save(user);

        UserEvent event = UserEvent.of("user.updated", saved.getId(), saved.getEmail(), saved.getName());
        eventPublisher.publishEvent(new UserUpdatedEvent(event));

        return userMapper.toResponse(saved);
    }

    private void assertSelf(UUID requestedId, UUID authenticatedUserId) {
        if (!requestedId.equals(authenticatedUserId)) {
            throw new UserAccessDeniedException();
        }
    }
}