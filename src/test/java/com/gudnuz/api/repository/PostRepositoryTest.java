package com.gudnuz.api.repository;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.Categorie;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
public class PostRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PostRepository postRepository;

    @Test
    void findByCreatedAtAfterAndCategorieAndStatutNotOrderByCreatedAtDesc_filtreCorrectement() {

        User user = User.builder()
                .pseudo("Tester")
                .email("tester@gudnuz.com")
                .password("hash")
                .role(Role.USER)
                .build();
        testEntityManager.persist(user);

        Post postTropVieux = Post.builder()
                .titre("Vieux post")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .user(user)
                .build();
        testEntityManager.persist(postTropVieux);

        testEntityManager.getEntityManager()
                .createQuery("UPDATE Post p SET p.createdAt = :date WHERE p.id = :id")
                .setParameter("date", LocalDateTime.now().minusHours(30))
                .setParameter("id", postTropVieux.getId())
                .executeUpdate();

        Post bonPost = Post.builder()
                .titre("Bon post")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .user(user)
                .build();
        testEntityManager.persist(bonPost);

        Post postHumour = Post.builder()
                .titre("post humour")
                .contenu("contenu")
                .categorie(Categorie.HUMOUR)
                .statut(null)
                .user(user)
                .build();
        testEntityManager.persist(postHumour);

        Post postActuRemoved = Post.builder()
                .titre("Vieux post")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .statut(PostStatut.REMOVED)
                .user(user)
                .build();
        testEntityManager.persist(postActuRemoved);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Post> resultat = postRepository.findByCreatedAtAfterAndCategorieAndStatutNotOrderByCreatedAtDesc(
                LocalDateTime.now().minusHours(24),
                Categorie.ACTU,
                PostStatut.REMOVED
        );


        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getTitre()).isEqualTo("Bon post");

    }




    @Test
    void findByUserIdAndStatutNotOrderByCreatedAtDescTest() {

        User user = User.builder()
                .pseudo("Tester")
                .email("tester@gudnuz.com")
                .password("hash")
                .role(Role.USER)
                .build();
        testEntityManager.persist(user);

        Post postRemoved = Post.builder()
                .titre("Post Supprimé")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .statut(PostStatut.REMOVED)
                .user(user)
                .build();
        testEntityManager.persist(postRemoved);

        Post bonPost = Post.builder()
                .titre("Bon post")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .statut(null)
                .user(user)
                .build();
        testEntityManager.persist(bonPost);


        testEntityManager.flush();
        testEntityManager.clear();

        List<Post> resultat = postRepository.findByUserIdAndStatutNotOrderByCreatedAtDesc(
                user.getId(),
                PostStatut.REMOVED
        );


        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getTitre()).isEqualTo("Bon post");

    }

    @Test
    void findTopPostsSinceTest() {

        User user = User.builder()
                .pseudo("Tester")
                .email("tester@gudnuz.com")
                .password("hash")
                .role(Role.USER)
                .build();
        testEntityManager.persist(user);


        Post bonPostFive = Post.builder()
                .titre("Bon post five")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .sparksCount(5)
                .statut(null)
                .user(user)
                .build();
        testEntityManager.persist(bonPostFive);

        Post bonPostTwo = Post.builder()
                .titre("Bon post")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .sparksCount(2)
                .statut(null)
                .user(user)
                .build();
        testEntityManager.persist(bonPostTwo);

        Post bonPostVieux = Post.builder()
                .titre("Bon post")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .sparksCount(77)
                .statut(null)
                .user(user)
                .build();
        testEntityManager.persist(bonPostVieux);

        testEntityManager.getEntityManager()
                .createQuery("UPDATE Post p SET p.createdAt = :date WHERE p.id = :id")
                .setParameter("date", LocalDateTime.now().minusHours(30))
                .setParameter("id", bonPostVieux.getId())
                .executeUpdate();


        Post postRemoved = Post.builder()
                .titre("Post Supprimé")
                .contenu("contenu")
                .categorie(Categorie.ACTU)
                .statut(PostStatut.REMOVED)
                .user(user)
                .build();
        testEntityManager.persist(postRemoved);




        testEntityManager.flush();
        testEntityManager.clear();

        List<Post> resultat = postRepository.findTopPostsSince(LocalDateTime.now().minusHours(24));


        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getTitre()).isEqualTo("Bon post five");
        assertThat(resultat.get(0).getSparksCount()).isEqualTo(5);
        assertThat(resultat.get(1).getSparksCount()).isEqualTo(2);

    }
}