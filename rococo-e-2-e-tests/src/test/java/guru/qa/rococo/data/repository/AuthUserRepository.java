package guru.qa.rococo.data.repository;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Nonnull
    public AuthUserEntity create(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Nonnull
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(AuthUserEntity.class, id)
        );
    }

    @Nonnull
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("select u from AuthUserEntity u where u.username =: username", AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}