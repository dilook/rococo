package guru.qa.rococo.service;


import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
        userRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
                        () -> {
                            LOG.info("### Kafka consumer record: {}", cr.toString());

                            UserEntity userDataEntity = new UserEntity();
                            userDataEntity.setUsername(user.username());
                            UserEntity userEntity = userRepository.save(userDataEntity);

                            LOG.info(
                                    "### User '{}' successfully saved to database with id: {}",
                                    user.username(),
                                    userEntity.getId()
                            );
                        }
                );
    }

    @Transactional(readOnly = true)
    public @Nonnull UserJson getUser(@Nonnull String username) {
        return userRepository.findByUsername(username).map(UserJson::fromEntity).orElseThrow( () ->
                new NotFoundException("user with name '%s' not found".formatted(username)));
    }

    @Transactional
    public UserJson update(String username, UserJson user) {
        user.addUsername(username);
        UserEntity userEntity = userRepository.findByUsername(user.username())
                .orElseGet(() -> {
                    UserEntity emptyUser = new UserEntity();
                    emptyUser.setUsername(user.username());
                    return emptyUser;
                });

        userEntity.setFirstname(user.firstname());
        userEntity.setLastname(user.lastname());
        userEntity.setAvatar(user.avatar() != null ? user.avatar().getBytes(StandardCharsets.UTF_8): null);
        UserEntity saved = userRepository.save(userEntity);
        return UserJson.fromEntity(saved);
    }
}
