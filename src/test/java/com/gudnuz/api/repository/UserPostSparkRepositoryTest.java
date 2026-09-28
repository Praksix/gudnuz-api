package com.gudnuz.api.repository;

import com.gudnuz.api.dto.response.TopUserResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.UserPostSpark;
import com.gudnuz.api.model.enums.Categorie;
import com.gudnuz.api.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
public class UserPostSparkRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserPostSparkRepository userPostSparkRepository;

    @Test
    void findTopUsers_trieParSparksRecusEtExclutLesComptesSupprimes() {

        // Auteurs des posts (ce sont eux qui doivent apparaître dans le classement)
        User alice = persistUser("Alice", false);
        User david = persistUser("David", false);
        User supprime = persistUser("Supprime", true);

        // Users qui donnent les Sparks (ils ne reçoivent rien, donc absents du classement)
        User fan1 = persistUser("Fan1", false);
        User fan2 = persistUser("Fan2", false);
        User fan3 = persistUser("Fan3", false);

        // Alice : 2 posts → 2 + 1 = 3 Sparks (vérifie la somme sur plusieurs posts)
        Post postAlice1 = persistPost("Post Alice 1", alice);
        Post postAlice2 = persistPost("Post Alice 2", alice);
        persistSpark(fan1, postAlice1);
        persistSpark(fan2, postAlice1);
        persistSpark(fan1, postAlice2);

        // David : 1 Spark
        Post postDavid = persistPost("Post David", david);
        persistSpark(fan1, postDavid);

        // Compte supprimé : 3 Sparks — serait ex aequo en tête s'il n'était pas filtré
        Post postSupprime = persistPost("Post Supprime", supprime);
        persistSpark(fan1, postSupprime);
        persistSpark(fan2, postSupprime);
        persistSpark(fan3, postSupprime);

        testEntityManager.flush();
        testEntityManager.clear();

        List<TopUserResponse> resultat = userPostSparkRepository.findTopUsers(PageRequest.of(0, 10));


        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPseudo()).isEqualTo("Alice");
        assertThat(resultat.get(0).getSparksCount()).isEqualTo(3L);
        assertThat(resultat.get(1).getPseudo()).isEqualTo("David");
        assertThat(resultat.get(1).getSparksCount()).isEqualTo(1L);
        assertThat(resultat).extracting(TopUserResponse::getPseudo).doesNotContain("Supprime");

    }

    @Test
    void findTopUsers_respecteLaLimiteDuPageable() {

        User alice = persistUser("Alice", false);
        User david = persistUser("David", false);
        User fan1 = persistUser("Fan1", false);
        User fan2 = persistUser("Fan2", false);

        Post postAlice = persistPost("Post Alice", alice);
        persistSpark(fan1, postAlice);
        persistSpark(fan2, postAlice);

        Post postDavid = persistPost("Post David", david);
        persistSpark(fan1, postDavid);

        testEntityManager.flush();
        testEntityManager.clear();

        List<TopUserResponse> resultat = userPostSparkRepository.findTopUsers(PageRequest.of(0, 2));


        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPseudo()).isEqualTo("Alice");

    }


    private User persistUser(String pseudo, boolean deleted) {
        User user = User.builder()
                .pseudo(pseudo)
                .email(pseudo.toLowerCase() + "@gudnuz.com")
                .password("hash")
                .role(Role.USER)
                .deleted(deleted)
                .build();
        return testEntityManager.persist(user);
    }

    private Post persistPost(String titre, User auteur) {
        Post post = Post.builder()
                .titre(titre)
                .contenu("contenu")
                .categorie(Categorie.HUMOUR)
                .user(auteur)
                .build();
        return testEntityManager.persist(post);
    }

    private void persistSpark(User fan, Post post) {
        testEntityManager.persist(UserPostSpark.builder()
                .user(fan)
                .post(post)
                .build());
    }
}
