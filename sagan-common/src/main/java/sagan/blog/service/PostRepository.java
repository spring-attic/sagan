package sagan.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sagan.team.MemberProfile;
import sagan.blog.Post;
import sagan.blog.PostCategory;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategoryAndDraftFalse(PostCategory category, Pageable pageable);

    Page<Post> findByDraftTrue(Pageable pageRequest);

    Page<Post> findByDraftFalseAndPublishAtBefore(Date publishedBefore, Pageable pageRequest);

    List<Post> findByDraftFalseAndPublishAtBefore(Date publishedBefore);

    Page<Post> findByCategoryAndDraftFalseAndPublishAtBefore(PostCategory category, Date publishedBefore, Pageable pageRequest);

    Page<Post> findByBroadcastAndDraftFalseAndPublishAtBefore(boolean broadcast, Date publishedBefore, Pageable pageRequest);

    Page<Post> findByDraftFalseAndPublishAtAfter(Date now, Pageable pageRequest);

    Page<Post> findByDraftFalseAndAuthorAndPublishAtBefore(MemberProfile profile, Date publishedBefore, Pageable pageRequest);

    Post findByTitle(String title);

    Post findByTitleAndCreatedAt(String title, Date createdAt);

    Post findByPublicSlugAndDraftFalseAndPublishAtBefore(String publicSlug, Date now);

    @Query("select p from Post p where YEAR(p.publishAt) = ?1 and MONTH(p.publishAt) = ?2 and DAY(p.publishAt) = ?3")
    Page<Post> findByDate(int year, int month, int day, Pageable pageRequest);

    @Query("select p from Post p where YEAR(p.publishAt) = ?1 and MONTH(p.publishAt) = ?2")
    Page<Post> findByDate(int year, int month, Pageable pageRequest);

    @Query("select p from Post p where YEAR(p.publishAt) = ?1")
    Page<Post> findByDate(int year, Pageable pageRequest);
}
